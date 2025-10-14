package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.StageLessonRequestDto;
import com.devmam.taraacademyapi.models.dto.response.StageLessonResponseDto;
import com.devmam.taraacademyapi.models.entities.StageLesson;
import com.devmam.taraacademyapi.service.impl.entities.StageLessonService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/stage-lessons")
@PreAuthorize("permitAll()")
public class StageLessonController extends BaseController<StageLesson, Integer, StageLessonRequestDto, StageLessonResponseDto> {

    public StageLessonController(StageLessonService stageLessonService) {
        super(stageLessonService);
    }

    @Override
    protected StageLessonResponseDto toResponseDto(StageLesson stageLesson) {
        return StageLessonResponseDto.toDTO(stageLesson);
    }

    @Override
    protected StageLesson toEntity(StageLessonRequestDto requestDto) {
        StageLesson stageLesson = new StageLesson();
        stageLesson.setName(requestDto.getName());
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
