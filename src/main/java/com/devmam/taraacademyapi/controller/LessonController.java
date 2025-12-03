package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.LessonCreatingDto;
import com.devmam.taraacademyapi.models.dto.request.LessonRequestDto;
import com.devmam.taraacademyapi.models.dto.response.LessonResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Lesson;
import com.devmam.taraacademyapi.models.entities.StageLesson;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.repository.QuizRepository;
import com.devmam.taraacademyapi.service.impl.entities.LessonService;
import com.devmam.taraacademyapi.service.impl.entities.StageLessonService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/lessons")
@PreAuthorize("permitAll()")
public class LessonController extends BaseController<Lesson, Integer, LessonRequestDto, LessonResponseDto> {

    @Autowired
    private StageLessonService stageLessonService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserService userService;

    public LessonController(LessonService lessonService) {
        super(lessonService);
    }

    @PostMapping(
            value = "/creating",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseData<LessonResponseDto>> createLesson(
            @RequestPart("lessonDto") LessonCreatingDto requestDto,
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return lessonService.createLessonWithVideo(authHeader, requestDto, file);
    }

    @Override
    protected LessonResponseDto toResponseDto(Lesson lesson) {
        return LessonResponseDto.toDTO(lesson);
    }

    @Override
    protected Lesson toEntity(LessonRequestDto requestDto) {
        // Get stage entity
        StageLesson stage = null;
        if (requestDto.getStageId() != null) {
            stage = stageLessonService.getOne(requestDto.getStageId()).orElse(null);
        }

        // Get createdBy user entity
        User createdBy = null;
        if (requestDto.getCreatedBy() != null) {
            createdBy = userService.getOne(requestDto.getCreatedBy()).orElse(null);
        }

        Lesson lesson = new Lesson();
        lesson.setStage(stage);
        lesson.setTitle(requestDto.getTitle());
        lesson.setContent(requestDto.getContent());
        lesson.setVideoUrl(requestDto.getVideoUrl());
        lesson.setOrderIndex(requestDto.getOrderIndex() != null ? requestDto.getOrderIndex() : 0);
        lesson.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        lesson.setCreatedBy(createdBy);
        lesson.setIsDeleted(0);
        lesson.setCreatedAt(Instant.now());
        lesson.setUpdatedAt(Instant.now());

        return lesson;
    }

    @Override
    protected Page<LessonResponseDto> convertPage(Page<Lesson> lessonPage) {
        return LessonResponseDto.convertPage(lessonPage);
    }

    @Override
    protected String getEntityName() {
        return "Lesson";
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<LessonResponseDto>> getById(@PathVariable Integer id) {
        try {
            Optional<Lesson> entity = lessonService.getOne(id);
            if (entity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<LessonResponseDto>builder()
                                .status(404)
                                .message(getEntityName() + " not found")
                                .error(getEntityName() + " with id " + id + " not found")
                                .data(null)
                                .build());
            }

            Lesson lesson = entity.get();
            Long quizCount = quizRepository.countByLessonId(lesson.getId());
            LessonResponseDto responseDto = LessonResponseDto.toDTOWithQuizCount(lesson, quizCount);
            
            return ResponseEntity.ok(ResponseData.<LessonResponseDto>builder()
                    .status(200)
                    .message(getEntityName() + " found")
                    .error(null)
                    .data(responseDto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<LessonResponseDto>builder()
                            .status(500)
                            .message("Failed to get " + getEntityName())
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }
}
