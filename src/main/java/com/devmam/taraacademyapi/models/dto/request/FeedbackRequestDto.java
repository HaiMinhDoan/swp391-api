package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

/**
 * DTO for Feedback creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FeedbackRequestDto {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @Size(max = 100, message = "Reference type must not exceed 100 characters")
    private String referenceType;
    
    private Integer referenceId;
    
    private UUID referenceUserId;
    
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    private String comment;
    
    private Integer status;
    
    private String imgUrl;
}
