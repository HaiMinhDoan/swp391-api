package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Certificate;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Certificate}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CertificateResponseDto implements Serializable {
    private final Integer id;
    private final UUID userId;
    private final String userUsername;
    private final Integer courseId;
    private final String courseTitle;
    private final String imgUrl;
    private final UUID createdById;
    private final String createdByUsername;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static CertificateResponseDto toDTO(Certificate certificate) {
        return CertificateResponseDto.builder()
                .id(certificate.getId())
                .userId(certificate.getUser() != null ? certificate.getUser().getId() : null)
                .userUsername(certificate.getUser() != null ? certificate.getUser().getUsername() : null)
                .courseId(certificate.getCourse() != null ? certificate.getCourse().getId() : null)
                .courseTitle(certificate.getCourse() != null ? certificate.getCourse().getTitle() : null)
                .imgUrl(certificate.getImgUrl())
                .createdById(certificate.getCreatedBy() != null ? certificate.getCreatedBy().getId() : null)
                .createdByUsername(certificate.getCreatedBy() != null ? certificate.getCreatedBy().getUsername() : null)
                .createdAt(certificate.getCreatedAt())
                .updatedAt(certificate.getUpdatedAt())
                .status(certificate.getStatus())
                .isDeleted(certificate.getIsDeleted())
                .build();
    }

    public static Page<CertificateResponseDto> convertPage(Page<Certificate> certificatePage) {
        List<CertificateResponseDto> certificateResponseDTOs = certificatePage.getContent()
                .stream()
                .map(CertificateResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                certificateResponseDTOs,
                certificatePage.getPageable(),
                certificatePage.getTotalElements()
        );
    }
}
