package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Feedback;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Feedback}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FeedbackResponseDto implements Serializable {
    private final Integer id;
    private final UUID userId;
    private final String userUsername;
    private final String referenceType;
    private final Integer referenceId;
    private final UUID referenceUserId;
    private final Integer rating;
    private final String comment;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;
    private final String imgUrl;


    public static FeedbackResponseDto toDTO(Feedback feedback) {
        return FeedbackResponseDto.builder()
                .id(feedback.getId())
                .userId(feedback.getUser() != null ? feedback.getUser().getId() : null)
                .userUsername(feedback.getUser() != null ? feedback.getUser().getUsername() : null)
                .referenceType(feedback.getReferenceType())
                .referenceId(feedback.getReferenceId())
                .referenceUserId(feedback.getReferenceUserId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .status(feedback.getStatus())
                .isDeleted(feedback.getIsDeleted())
                .imgUrl(feedback.getImgUrl())
                .build();
    }

    public static Page<FeedbackResponseDto> convertPage(Page<Feedback> feedbackPage) {
        List<FeedbackResponseDto> feedbackResponseDTOs = feedbackPage.getContent()
                .stream()
                .map(FeedbackResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                feedbackResponseDTOs,
                feedbackPage.getPageable(),
                feedbackPage.getTotalElements()
        );
    }
}
