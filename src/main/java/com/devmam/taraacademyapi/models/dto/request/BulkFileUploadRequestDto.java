package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for bulk file upload
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BulkFileUploadRequestDto {
    
    @NotNull(message = "Files are required")
    @Size(min = 1, max = 10, message = "Must upload between 1 and 10 files")
    private java.util.List<org.springframework.web.multipart.MultipartFile> files;
    
    @Size(max = 100, message = "File type must not exceed 100 characters")
    private String fileType;

    @Size(max = 100, message = "File ref must not exceed 100 characters")
    private String fileRef;
    
    private Integer referenceId;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private Integer status;
}
