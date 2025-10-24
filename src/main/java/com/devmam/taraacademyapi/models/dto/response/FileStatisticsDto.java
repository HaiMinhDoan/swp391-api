package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;

/**
 * DTO for file statistics
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FileStatisticsDto {
    private Long totalFiles;
    private Long totalSize;
    private Long averageFileSize;
    private java.util.Map<String, Long> filesByType;
    private java.util.Map<String, Long> filesByStatus;
    private Long filesUploadedToday;
    private Long filesUploadedThisWeek;
    private Long filesUploadedThisMonth;
}
