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
    private Integer id;
    private  Integer categoryId;
    private  String categoryName;
    private  String thumbnail;
    private  String title;
    private  String summary;
    private  String description;
    private  String lang;
    private  BigDecimal price;
    private  Integer saleOff;
    private  UUID createdById;
    private  String createdByUsername;
    private  String createdByFullName;
    private  String createdByPhone;
    private  String createdByAvatarUrl;
    private  String createdByUserUsername;
    private  String createdByEmail;
    private  Instant createdAt;
    private  Instant updatedAt;
    private  Integer status;
    private  Integer isDeleted;

    public static CourseResponseDto toDTO(Course course) {
        return CourseResponseDto.builder()
                .id(course.getId())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .thumbnail(course.getThumbnail())
                .title(course.getTitle())
                .summary(course.getSummary())
                .description(course.getDescription())
                .lang(course.getLang())
                .price(course.getPrice())
                .saleOff(course.getSaleOff())
                .createdById(course.getCreatedBy() != null ? course.getCreatedBy().getId() : null)
                .createdByUsername(course.getCreatedBy() != null ? course.getCreatedBy().getUsername() : null)
                .createdByFullName(course.getCreatedBy() != null ? course.getCreatedBy().getFullName() : null)
                .createdByPhone(course.getCreatedBy() != null ? course.getCreatedBy().getPhone() : null)
                .createdByAvatarUrl(course.getCreatedBy() != null ? course.getCreatedBy().getAvt() : null)
                .createdByUserUsername(course.getCreatedBy() != null ? course.getCreatedBy().getUsername() : null)
                .createdByEmail(course.getCreatedBy() != null ? course.getCreatedBy().getEmail() : null)
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