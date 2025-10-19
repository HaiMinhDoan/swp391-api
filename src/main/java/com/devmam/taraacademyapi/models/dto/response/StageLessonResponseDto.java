package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.StageLesson;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.StageLesson}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class StageLessonResponseDto implements Serializable {
    private final Integer id;
    private final String name;
    private final String description;
    private final Integer orderIndex;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static StageLessonResponseDto toDTO(StageLesson stageLesson) {
        return StageLessonResponseDto.builder()
                .id(stageLesson.getId())
//                .name(stageLesson.getName())
                .description(stageLesson.getDescription())
                .orderIndex(stageLesson.getOrderIndex())
                .createdAt(stageLesson.getCreatedAt())
                .updatedAt(stageLesson.getUpdatedAt())
                .status(stageLesson.getStatus())
                .isDeleted(stageLesson.getIsDeleted())
                .build();
    }

    public static Page<StageLessonResponseDto> convertPage(Page<StageLesson> stageLessonPage) {
        List<StageLessonResponseDto> stageLessonResponseDTOs = stageLessonPage.getContent()
                .stream()
                .map(StageLessonResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                stageLessonResponseDTOs,
                stageLessonPage.getPageable(),
                stageLessonPage.getTotalElements()
        );
    }
}
