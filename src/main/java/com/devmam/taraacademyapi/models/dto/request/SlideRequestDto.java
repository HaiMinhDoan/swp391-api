package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for Slide creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SlideRequestDto {
    
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Image URL is required")
    @NotNull(message = "Image URL cannot be null")
    private String imageUrl;
    
    private String linkUrl;
    
    private Integer orderIndex;
    
    private Integer status;
}
