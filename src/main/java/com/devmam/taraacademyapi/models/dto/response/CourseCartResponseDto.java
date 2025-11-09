package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.CourseCart;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.CourseCart}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCartResponseDto implements Serializable {
    Integer id;
    UserResponseDto user;
    CourseResponseDto course;
    Instant createdAt;
    Instant updatedAt;
    Integer status;
    Integer isDeleted;

    public static CourseCartResponseDto toDto(CourseCart courseCart) {
        return CourseCartResponseDto.builder()
                .id(courseCart.getId())
                .user(UserResponseDto.toDto(courseCart.getUser()))
                .course(CourseResponseDto.toDto(courseCart.getCourse()))
                .createdAt(courseCart.getCreatedAt())
                .updatedAt(courseCart.getUpdatedAt())
                .status(courseCart.getStatus())
                .isDeleted(courseCart.getIsDeleted())
                .build();
    }

    public static Page<CourseCartResponseDto> convertPage(Page<CourseCart> courseCartPage) {
        List<CourseCartResponseDto> courseCartResponseDTOs = courseCartPage.getContent()
                .stream()
                .map(CourseCartResponseDto::toDto)
                .collect(java.util.stream.Collectors.toList());
        return new PageImpl<>(
                courseCartResponseDTOs,
                courseCartPage.getPageable(),
                courseCartPage.getTotalElements()
        );
    }
}