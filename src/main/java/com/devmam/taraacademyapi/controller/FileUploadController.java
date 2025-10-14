package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.FileUploadRequestDto;
import com.devmam.taraacademyapi.models.dto.response.FileUploadResponseDto;
import com.devmam.taraacademyapi.models.entities.FileUpload;
import com.devmam.taraacademyapi.service.impl.entities.FileUploadService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/file-uploads")
@PreAuthorize("permitAll()")
public class FileUploadController extends BaseController<FileUpload, Integer, FileUploadRequestDto, FileUploadResponseDto> {

    public FileUploadController(FileUploadService fileUploadService) {
        super(fileUploadService);
    }

    @Override
    protected FileUploadResponseDto toResponseDto(FileUpload fileUpload) {
        return FileUploadResponseDto.toDTO(fileUpload);
    }

    @Override
    protected FileUpload toEntity(FileUploadRequestDto requestDto) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName(requestDto.getFileName());
        fileUpload.setFilePath(requestDto.getFilePath());
        fileUpload.setFileType(requestDto.getFileType());
        fileUpload.setFileSize(requestDto.getFileSize());
        fileUpload.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        fileUpload.setIsDeleted(0);
        fileUpload.setCreatedAt(Instant.now());
        fileUpload.setUpdatedAt(Instant.now());

        return fileUpload;
    }

    @Override
    protected Page<FileUploadResponseDto> convertPage(Page<FileUpload> fileUploadPage) {
        return FileUploadResponseDto.convertPage(fileUploadPage);
    }

    @Override
    protected String getEntityName() {
        return "FileUpload";
    }
}
