package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Course;
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
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Course}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CourseResponseDto implements Serializable {
    private final Integer id;
    private final Integer categoryId;
    private final String categoryName;
    private final String thumnail;
    private final String title;
    private final String summary;
    private final String description;
    private final String lang;
    private final BigDecimal price;
    private final Integer saleOff;
    private final UUID createdById;
    private final String createdByUsername;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static CourseResponseDto toDTO(Course course) {
        return CourseResponseDto.builder()
                .id(course.getId())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .thumnail(course.getThumnail())
                .title(course.getTitle())
                .summary(course.getSummary())
                .description(course.getDescription())
                .lang(course.getLang())
                .price(course.getPrice())
                .saleOff(course.getSaleOff())
                .createdById(course.getCreatedBy() != null ? course.getCreatedBy().getId() : null)
                .createdByUsername(course.getCreatedBy() != null ? course.getCreatedBy().getUsername() : null)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .status(course.getStatus())
                .isDeleted(course.getIsDeleted())
                .build();
    }

    public static Page<CourseResponseDto> convertPage(Page<Course> coursePage) {
        List<CourseResponseDto> courseResponseDTOs = coursePage.getContent()
                .stream()
                .map(CourseResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                courseResponseDTOs,
                coursePage.getPageable(),
                coursePage.getTotalElements()
        );
    }
}