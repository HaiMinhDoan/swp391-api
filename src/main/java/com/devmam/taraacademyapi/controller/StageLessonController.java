package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.StageLessonRequestDto;
import com.devmam.taraacademyapi.models.dto.response.LessonResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.StageLessonResponseDto;
import com.devmam.taraacademyapi.models.dto.response.StageLessonWithLessonsResponseDto;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.Lesson;
import com.devmam.taraacademyapi.models.entities.StageLesson;
import com.devmam.taraacademyapi.repository.LessonRepository;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.StageLessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/stage-lessons")
@PreAuthorize("permitAll()")
public class StageLessonController extends BaseController<StageLesson, Integer, StageLessonRequestDto, StageLessonResponseDto> {

    @Autowired
    private CourseService courseService;

    @Autowired
    private LessonRepository lessonRepository;

    private final StageLessonService stageLessonService;

    public StageLessonController(StageLessonService stageLessonService) {
        super(stageLessonService);
        this.stageLessonService = stageLessonService;
    }

    @Override
    protected StageLessonResponseDto toResponseDto(StageLesson stageLesson) {
        return StageLessonResponseDto.toDTO(stageLesson);
    }

    @Override
    protected StageLesson toEntity(StageLessonRequestDto requestDto) {
        // Get course entity
        Course course = null;
        if (requestDto.getCourseId() != null) {
            course = courseService.getOne(requestDto.getCourseId()).orElse(null);
        }
        
        StageLesson stageLesson = new StageLesson();
        stageLesson.setCourse(course);
        stageLesson.setTitle(requestDto.getName());
        stageLesson.setDescription(requestDto.getDescription());
        stageLesson.setOrderIndex(requestDto.getOrderIndex() != null ? requestDto.getOrderIndex() : 0);
        stageLesson.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        stageLesson.setIsDeleted(0);
        stageLesson.setCreatedAt(Instant.now());
        stageLesson.setUpdatedAt(Instant.now());

        return stageLesson;
    }

    @Override
    protected Page<StageLessonResponseDto> convertPage(Page<StageLesson> stageLessonPage) {
        return StageLessonResponseDto.convertPage(stageLessonPage);
    }

    @Override
    protected String getEntityName() {
        return "StageLesson";
    }

    @GetMapping("/by-course")
    public ResponseEntity<ResponseData<List<StageLessonWithLessonsResponseDto>>> getStageLessonsByCourseId(
            @RequestParam Integer courseId) {
        try {
            List<StageLesson> stageLessons = stageLessonService.findByCourseId(courseId);
            
            List<StageLessonWithLessonsResponseDto> result = stageLessons.stream()
                    .map(stageLesson -> {
                        // Get lessons for this stage
                        List<Lesson> lessons = lessonRepository.findByStageId(stageLesson.getId());
                        List<LessonResponseDto> lessonDtos = lessons.stream()
                                .map(lesson -> {
                                    Long quizCount = 0L;
                                    try {
                                        quizCount = quizRepository.countByLessonId(lesson.getId());
                                    } catch (Exception ignored) {
                                    }
                                    return LessonResponseDto.toDTOWithQuizCount(lesson, quizCount);
                                })
                                .collect(Collectors.toList());
                        
                        return StageLessonWithLessonsResponseDto.builder()
                                .id(stageLesson.getId())
                                .name(stageLesson.getTitle())
                                .description(stageLesson.getDescription())
                                .orderIndex(stageLesson.getOrderIndex())
                                .createdAt(stageLesson.getCreatedAt())
                                .updatedAt(stageLesson.getUpdatedAt())
                                .status(stageLesson.getStatus())
                                .isDeleted(stageLesson.getIsDeleted())
                                .lessons(lessonDtos)
                                .build();
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ResponseData.<List<StageLessonWithLessonsResponseDto>>builder()
                    .status(200)
                    .message("Stage lessons retrieved successfully")
                    .error(null)
                    .data(result)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<StageLessonWithLessonsResponseDto>>builder()
                            .status(500)
                            .message("Failed to get stage lessons")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }
}
