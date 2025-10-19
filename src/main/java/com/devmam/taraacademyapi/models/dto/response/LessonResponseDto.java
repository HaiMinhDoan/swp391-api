package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Lesson;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Lesson}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LessonResponseDto implements Serializable {
    private final Integer id;
    private final Integer stageId;
    private final String stageName;
    private final String title;
    private final String content;
    private final String videoUrl;
    private final Integer orderIndex;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static LessonResponseDto toDTO(Lesson lesson) {
        return LessonResponseDto.builder()
                .id(lesson.getId())
                .stageId(lesson.getStage() != null ? lesson.getStage().getId() : null)
                .stageName(lesson.getStage() != null ? lesson.getStage().getTitle() : null)
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .orderIndex(lesson.getOrderIndex())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .status(lesson.getStatus())
                .isDeleted(lesson.getIsDeleted())
                .build();
    }

    public static Page<LessonResponseDto> convertPage(Page<Lesson> lessonPage) {
        List<LessonResponseDto> lessonResponseDTOs = lessonPage.getContent()
                .stream()
                .map(LessonResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                lessonResponseDTOs,
                lessonPage.getPageable(),
                lessonPage.getTotalElements()
        );
    }
}
