package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;

import java.util.List;

/**
 * DTO for bulk file upload response
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BulkFileUploadResultDto {
    private List<FileUploadResultDto> results;
    private Integer totalFiles;
    private Integer successfulUploads;
    private Integer failedUploads;
    private Long totalSize;
    private String overallStatus;
}
