package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Ser;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Ser}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SerResponseDto implements Serializable {
    private final Integer id;
    private final String name;
    private final String description;
    private final String detail;
    private final BigDecimal price;
    private final String thumnail;
    private final UUID createdById;
    private final String createdByUsername;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static SerResponseDto toDTO(Ser ser) {
        return SerResponseDto.builder()
                .id(ser.getId())
                .name(ser.getName())
                .description(ser.getDescription())
                .detail(ser.getDetail())
                .price(ser.getPrice())
                .thumnail(ser.getThumnail())
                .createdById(ser.getCreatedBy() != null ? ser.getCreatedBy().getId() : null)
                .createdByUsername(ser.getCreatedBy() != null ? ser.getCreatedBy().getUsername() : null)
                .createdAt(ser.getCreatedAt())
                .updatedAt(ser.getUpdatedAt())
                .status(ser.getStatus())
                .isDeleted(ser.getIsDeleted())
                .build();
    }

    public static Page<SerResponseDto> convertPage(Page<Ser> serPage) {
        List<SerResponseDto> serResponseDTOs = serPage.getContent()
                .stream()
                .map(SerResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                serResponseDTOs,
                serPage.getPageable(),
                serPage.getTotalElements()
        );
    }
}
