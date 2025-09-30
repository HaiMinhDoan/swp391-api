package com.devmam.swp391api.models.dto.response;

import com.devmam.swp391api.models.entities.Course;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for {@link Course}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDTO implements Serializable {
    final Integer id;
    final String thumnail;
    @Size(max = 255)
    final String title;
    @Size(max = 255)
    final String summary;
    final String description;
    @Size(max = 255)
    final String lang;
    final BigDecimal price;
    final Integer saleOff;
    final Instant createdAt;
    final Instant updatedAt;
    final Integer status;
    final Integer isDeleted;

    public static CourseDTO toDTO(Course m) {
        return CourseDTO.builder()
                .id(m.getId())
                .thumnail(m.getThumnail())
                .title(m.getTitle())
                .summary(m.getSummary())
                .description(m.getDescription())
                .lang(m.getLang())
                .price(m.getPrice())
                .saleOff(m.getSaleOff())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .status(m.getStatus())
                .isDeleted(m.getIsDeleted())
                .build();
    }
}