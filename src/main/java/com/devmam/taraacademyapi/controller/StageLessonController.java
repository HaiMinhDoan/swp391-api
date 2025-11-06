package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.StageLessonRequestDto;
import com.devmam.taraacademyapi.models.dto.response.StageLessonResponseDto;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.StageLesson;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.StageLessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/stage-lessons")
@PreAuthorize("permitAll()")
public class StageLessonController extends BaseController<StageLesson, Integer, StageLessonRequestDto, StageLessonResponseDto> {

    @Autowired
    private CourseService courseService;

    public StageLessonController(StageLessonService stageLessonService) {
        super(stageLessonService);
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
}
