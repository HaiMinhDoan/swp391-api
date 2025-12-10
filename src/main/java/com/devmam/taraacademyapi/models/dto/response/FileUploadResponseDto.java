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
    private Integer id;
    private String fileName;
    private String filePath;
    private String fileType;
    private String fileRef;
    private Long fileSize;
    private Integer referenceId;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer status;
    private Integer isDeleted;
    private String publicUrl;

    public static FileUploadResponseDto toDTO(FileUpload fileUpload) {
        return FileUploadResponseDto.builder()
                .id(fileUpload.getId())
                .fileName(fileUpload.getFileName())
                .filePath(fileUpload.getFilePath())
                .fileType(fileUpload.getFileType())
                .fileRef(fileUpload.getFileRef())
                .fileSize(fileUpload.getFileSize())
                .referenceId(fileUpload.getReferenceId())
                .description(fileUpload.getDescription())
                .createdAt(fileUpload.getCreatedAt())
                .updatedAt(fileUpload.getUpdatedAt())
                .status(fileUpload.getStatus())
                .isDeleted(fileUpload.getIsDeleted())
                .publicUrl(null) // Will be set by service layer
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
