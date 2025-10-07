package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.User;
import jakarta.validation.constraints.Size;
import kotlin._Assertions;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Course}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CourseResponseDTO implements Serializable {
    private final Integer id;
    private final String thumnail;
    @Size(max = 255)
    private final String title;
    @Size(max = 255)
    private final String summary;
    private final String description;
    @Size(max = 255)
    private final String lang;
    private final BigDecimal price;
    private final Integer saleOff;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static CourseResponseDTO toDTO(Course m) {
        return CourseResponseDTO.builder()
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

    public static Page<CourseResponseDTO> convertPage(Page<Course> coursePage) {
        List<CourseResponseDTO> courseResponseDTOs = coursePage.getContent()
                .stream()
                .map(CourseResponseDTO::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                courseResponseDTOs,
                coursePage.getPageable(),
                coursePage.getTotalElements()
        );
    }
}