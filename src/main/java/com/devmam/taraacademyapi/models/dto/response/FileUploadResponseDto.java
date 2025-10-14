package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.FileUpload;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.FileUpload}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FileUploadResponseDto implements Serializable {
    private final Integer id;
    private final String fileName;
    private final String filePath;
    private final String fileType;
    private final Long fileSize;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static FileUploadResponseDto toDTO(FileUpload fileUpload) {
        return FileUploadResponseDto.builder()
                .id(fileUpload.getId())
                .fileName(fileUpload.getFileName())
                .filePath(fileUpload.getFilePath())
                .fileType(fileUpload.getFileType())
                .fileSize(fileUpload.getFileSize())
                .createdAt(fileUpload.getCreatedAt())
                .updatedAt(fileUpload.getUpdatedAt())
                .status(fileUpload.getStatus())
                .isDeleted(fileUpload.getIsDeleted())
                .build();
    }

    public static Page<FileUploadResponseDto> convertPage(Page<FileUpload> fileUploadPage) {
        List<FileUploadResponseDto> fileUploadResponseDTOs = fileUploadPage.getContent()
                .stream()
                .map(FileUploadResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                fileUploadResponseDTOs,
                fileUploadPage.getPageable(),
                fileUploadPage.getTotalElements()
        );
    }
}
