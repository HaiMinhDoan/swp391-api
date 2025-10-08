package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Career;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Career}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CareerResponseDto implements Serializable {
    private final Integer id;
    private final String title;
    private final String description;
    private final UUID createdById;
    private final String createdByUsername;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static CareerResponseDto toDTO(Career career) {
        return CareerResponseDto.builder()
                .id(career.getId())
                .title(career.getTitle())
                .description(career.getDescription())
                .createdById(career.getCreatedBy() != null ? career.getCreatedBy().getId() : null)
                .createdByUsername(career.getCreatedBy() != null ? career.getCreatedBy().getUsername() : null)
                .createdAt(career.getCreatedAt())
                .updatedAt(career.getUpdatedAt())
                .status(career.getStatus())
                .isDeleted(career.getIsDeleted())
                .build();
    }

    public static Page<CareerResponseDto> convertPage(Page<Career> careerPage) {
        List<CareerResponseDto> careerResponseDTOs = careerPage.getContent()
                .stream()
                .map(CareerResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                careerResponseDTOs,
                careerPage.getPageable(),
                careerPage.getTotalElements()
        );
    }
}