package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.CourseCategory;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.CourseCategory}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CourseCategoryResponseDto implements Serializable {
    private final Integer id;
    private final String name;
    private final String description;
    private final UUID createdById;
    private final String createdByUsername;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static CourseCategoryResponseDto toDTO(CourseCategory courseCategory) {
        return CourseCategoryResponseDto.builder()
                .id(courseCategory.getId())
                .name(courseCategory.getName())
                .description(courseCategory.getDescription())
                .createdById(courseCategory.getCreatedBy() != null ? courseCategory.getCreatedBy().getId() : null)
                .createdByUsername(courseCategory.getCreatedBy() != null ? courseCategory.getCreatedBy().getUsername() : null)
                .createdAt(courseCategory.getCreatedAt())
                .updatedAt(courseCategory.getUpdatedAt())
                .status(courseCategory.getStatus())
                .isDeleted(courseCategory.getIsDeleted())
                .build();
    }

    public static Page<CourseCategoryResponseDto> convertPage(Page<CourseCategory> courseCategoryPage) {
        List<CourseCategoryResponseDto> courseCategoryResponseDTOs = courseCategoryPage.getContent()
                .stream()
                .map(CourseCategoryResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                courseCategoryResponseDTOs,
                courseCategoryPage.getPageable(),
                courseCategoryPage.getTotalElements()
        );
    }
}