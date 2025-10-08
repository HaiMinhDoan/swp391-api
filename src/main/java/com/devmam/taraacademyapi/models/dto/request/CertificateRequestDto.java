package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * DTO for Certificate creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CertificateRequestDto {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Course ID is required")
    private Integer courseId;
    
    private String imgUrl;
    
    private Integer status;
}
