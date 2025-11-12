package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.UserCourse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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
    private Integer id;
    private UUID userId;
    private String userUsername;
    private Integer courseId;
    private String courseTitle;
    private Instant enrolledAt;
    private Instant completedAt;
    private Integer progress;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer status;
    private Integer isDeleted;
    private Integer currentStageId;
    private Integer currentLessonId;

    public static UserCourseResponseDto toDTO(UserCourse userCourse) {
        return UserCourseResponseDto.builder()
                .id(userCourse.getId())
                .userId(userCourse.getUser() != null ? userCourse.getUser().getId() : null)
                .userUsername(userCourse.getUser() != null ? userCourse.getUser().getUsername() : null)
                .courseId(userCourse.getCourse() != null ? userCourse.getCourse().getId() : null)
                .courseTitle(userCourse.getCourse() != null ? userCourse.getCourse().getTitle() : null)
                .enrolledAt(userCourse.getEnrolledAt())
                .completedAt(userCourse.getCompletedAt())
                .progress(userCourse.getProgress().intValue())
                .currentStageId(userCourse.getCurrentStageId())
                .currentLessonId(userCourse.getCurrentLessonId())
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
