package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Slide;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Slide}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SlideResponseDto implements Serializable {
    private final Integer id;
    private final String title;
    private final String description;
    private final String imageUrl;
    private final String linkUrl;
    private final Integer orderIndex;
    private final UUID createdById;
    private final String createdByUsername;
    private final String createdByFullName;
    private final String createdByPhone;
    private final String createdByAvatarUrl;
    private final String createdByUserUsername;
    private final String createdByEmail;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static SlideResponseDto toDTO(Slide slide) {
        return SlideResponseDto.builder()
                .id(slide.getId())
                .title(slide.getTitle())
                .description(slide.getDescription())
                .imageUrl(slide.getImageUrl())
                .linkUrl(slide.getLinkUrl())
                .orderIndex(slide.getOrderIndex())
                .createdById(slide.getCreatedBy() != null ? slide.getCreatedBy().getId() : null)
                .createdByUsername(slide.getCreatedBy() != null ? slide.getCreatedBy().getUsername() : null)
                .createdByFullName(slide.getCreatedBy() != null ? slide.getCreatedBy().getFullName() : null)
                .createdByPhone(slide.getCreatedBy() != null ? slide.getCreatedBy().getPhone() : null)
                .createdByAvatarUrl(slide.getCreatedBy() != null ? slide.getCreatedBy().getAvt() : null)
                .createdByUserUsername(slide.getCreatedBy() != null ? slide.getCreatedBy().getUsername() : null)
                .createdByEmail(slide.getCreatedBy() != null ? slide.getCreatedBy().getEmail() : null)
                .createdAt(slide.getCreatedAt())
                .updatedAt(slide.getUpdatedAt())
                .status(slide.getStatus())
                .isDeleted(slide.getIsDeleted())
                .build();
    }

    public static Page<SlideResponseDto> convertPage(Page<Slide> slidePage) {
        List<SlideResponseDto> slideResponseDTOs = slidePage.getContent()
                .stream()
                .map(SlideResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                slideResponseDTOs,
                slidePage.getPageable(),
                slidePage.getTotalElements()
        );
    }
}