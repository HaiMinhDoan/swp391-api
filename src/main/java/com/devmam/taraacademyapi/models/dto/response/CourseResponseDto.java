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
    private  String rejectReason;

    public static CourseResponseDto toDto(Course course) {
        return CourseResponseDto.builder()
                .id(course.getId())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .thumbnail(replaceBaseUrl(course.getThumbnail(), "https://miniotaraacademy.io.vn"))
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
                .rejectReason(course.getRejectReason())
                .build();
    }

    public static List<CourseResponseDto> toDtoList(List<Course> courseList) {
        return courseList.stream()
                .map(CourseResponseDto::toDto)
                .toList();
    }

    public static Page<CourseResponseDto> convertPage(Page<Course> coursePage) {
        List<CourseResponseDto> courseResponseDTOs = coursePage.getContent()
                .stream()
                .map(CourseResponseDto::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(
                courseResponseDTOs,
                coursePage.getPageable(),
                coursePage.getTotalElements()
        );
    }


    public static String replaceBaseUrl(String originalUrl, String newBaseUrl) {
        try {
            java.net.URL url = new java.net.URL(originalUrl);

            // Lấy path và port từ URL gốc
            String path = url.getFile();
            int port = url.getPort();

            // Nếu có port thì giữ lại, nếu không thì bỏ
            String portPart = (port == -1) ? "" : ":" + port;

            // Ghép lại URL mới
            return newBaseUrl + portPart + path;

        } catch (Exception e) {
            e.printStackTrace();
            return originalUrl; // nếu lỗi thì trả về URL gốc
        }

    }
}