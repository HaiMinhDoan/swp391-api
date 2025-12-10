package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for FileUpload creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FileUploadRequestDto {
    
    @NotNull(message = "File name is required")
    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;
    
    @Size(max = 255, message = "File path must not exceed 255 characters")
    private String filePath;
    
    @Size(max = 100, message = "File type must not exceed 100 characters")
    private String fileType;
    
    @Size(max = 100, message = "File ref must not exceed 100 characters")
    private String fileRef;
    
    private Long fileSize;
    
    private Integer referenceId;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private Integer status;
}
