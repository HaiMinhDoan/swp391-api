package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for file upload with multipart file
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FileUploadMultipartRequestDto {
    
    @NotNull(message = "File is required")
    private MultipartFile file;
    
    @Size(max = 100, message = "File type must not exceed 100 characters")
    private String fileType;
    
    private Integer referenceId;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private Integer status;
    
    private String customFileName;
}
