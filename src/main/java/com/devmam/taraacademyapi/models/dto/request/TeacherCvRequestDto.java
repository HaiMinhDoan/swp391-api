package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

/**
 * DTO for TeacherCv creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TeacherCvRequestDto {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @Size(max = 20, message = "Title must not exceed 20 characters")
    private String title;
    
    private String description;
    
    private String cvUrl;
    
    private String experience;
    
    private String skills;
    
    private String educations;
    
    private String certificates;
    
    private Integer status;
    
    @Size(max = 255, message = "Reject reason must not exceed 255 characters")
    private String rejectReason;
}
