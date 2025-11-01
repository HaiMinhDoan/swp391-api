package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.LessonCreatingDto;
import com.devmam.taraacademyapi.models.dto.request.LessonRequestDto;
import com.devmam.taraacademyapi.models.dto.response.LessonResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Lesson;
import com.devmam.taraacademyapi.models.entities.StageLesson;
import com.devmam.taraacademyapi.service.impl.entities.LessonService;
import com.devmam.taraacademyapi.service.impl.entities.StageLessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/lessons")
@PreAuthorize("permitAll()")
public class LessonController extends BaseController<Lesson, Integer, LessonRequestDto, LessonResponseDto> {

    @Autowired
    private StageLessonService stageLessonService;

    @Autowired
    private LessonService lessonService;

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

        Lesson lesson = new Lesson();
        lesson.setStage(stage);
        lesson.setTitle(requestDto.getTitle());
        lesson.setContent(requestDto.getContent());
        lesson.setVideoUrl(requestDto.getVideoUrl());
        lesson.setOrderIndex(requestDto.getOrderIndex() != null ? requestDto.getOrderIndex() : 0);
        lesson.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
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
}
