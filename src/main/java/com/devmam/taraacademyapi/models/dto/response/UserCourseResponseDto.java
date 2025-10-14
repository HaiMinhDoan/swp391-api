package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.UserCourse;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.UserCourse}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserCourseResponseDto implements Serializable {
    private final Integer id;
    private final UUID userId;
    private final String userUsername;
    private final Integer courseId;
    private final String courseTitle;
    private final Instant enrolledAt;
    private final Instant completedAt;
    private final Integer progress;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static UserCourseResponseDto toDTO(UserCourse userCourse) {
        return UserCourseResponseDto.builder()
                .id(userCourse.getId())
                .userId(userCourse.getUser() != null ? userCourse.getUser().getId() : null)
                .userUsername(userCourse.getUser() != null ? userCourse.getUser().getUsername() : null)
                .courseId(userCourse.getCourse() != null ? userCourse.getCourse().getId() : null)
                .courseTitle(userCourse.getCourse() != null ? userCourse.getCourse().getTitle() : null)
                .enrolledAt(userCourse.getEnrolledAt())
                .completedAt(userCourse.getCompletedAt())
                .progress(userCourse.getProgress())
                .createdAt(userCourse.getCreatedAt())
                .updatedAt(userCourse.getUpdatedAt())
                .status(userCourse.getStatus())
                .isDeleted(userCourse.getIsDeleted())
                .build();
    }

    public static Page<UserCourseResponseDto> convertPage(Page<UserCourse> userCoursePage) {
        List<UserCourseResponseDto> userCourseResponseDTOs = userCoursePage.getContent()
                .stream()
                .map(UserCourseResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                userCourseResponseDTOs,
                userCoursePage.getPageable(),
                userCoursePage.getTotalElements()
        );
    }
}
