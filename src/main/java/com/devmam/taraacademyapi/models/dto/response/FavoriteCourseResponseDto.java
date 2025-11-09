package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.CourseCart;
import com.devmam.taraacademyapi.models.entities.FavoriteCourse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.FavoriteCourse}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavoriteCourseResponseDto {
    Integer id;
    UserResponseDto user;
    CourseResponseDto course;
    Instant createdAt;
    Instant updatedAt;
    Integer status;
    Integer isDeleted;
    public static FavoriteCourseResponseDto toDto(FavoriteCourse favoriteCourse) {
        return FavoriteCourseResponseDto.builder()
                .id(favoriteCourse.getId())
                .user(UserResponseDto.toDto(favoriteCourse.getUser()))
                .course(CourseResponseDto.toDto(favoriteCourse.getCourse()))
                .createdAt(favoriteCourse.getCreatedAt())
                .updatedAt(favoriteCourse.getUpdatedAt())
                .status(favoriteCourse.getStatus())
                .isDeleted(favoriteCourse.getIsDeleted())
                .build();
    }

    public static Page<FavoriteCourseResponseDto> convertPage(Page<FavoriteCourse> favoriteCoursePage) {
        List<FavoriteCourseResponseDto> favoriteCourseResponseDTOs = favoriteCoursePage.getContent()
                .stream()
                .map(FavoriteCourseResponseDto::toDto)
                .collect(java.util.stream.Collectors.toList());
        return new PageImpl<>(
                favoriteCourseResponseDTOs,
                favoriteCoursePage.getPageable(),
                favoriteCoursePage.getTotalElements()
        );
    }
}
