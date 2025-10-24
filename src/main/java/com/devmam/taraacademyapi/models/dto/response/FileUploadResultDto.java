package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;

/**
 * DTO for file upload response with additional metadata
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FileUploadResultDto {
    private Integer id;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private String publicUrl;
    private String downloadUrl;
    private String presignedUploadUrl;
    private Boolean success;
    private String errorMessage;
    private java.time.Instant uploadedAt;
}
