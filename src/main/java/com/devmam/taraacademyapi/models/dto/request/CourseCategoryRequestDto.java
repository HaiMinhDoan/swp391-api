package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for CourseCategory creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CourseCategoryRequestDto {
    
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    private String description;
    
    private Integer status;
}
