package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.UserCourse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HistoryUserCourse {

    Integer id;
    UserResponseDto user;
    CourseResponseDto course;
    TranResponseDto tran;
    Instant enrolledAt;
    Instant expiredAt;
    Instant completedAt;
    BigDecimal progress;
    Instant createdAt;
    Instant updatedAt;
    Integer currentLessonId;
    Integer currentStageId;
    Integer status;
    Integer isDeleted;

    public static HistoryUserCourse toDTO(UserCourse userCourse) {
        return HistoryUserCourse.builder()
                .id(userCourse.getId())
                .user(UserResponseDto.toDto(userCourse.getUser()))
                .course(CourseResponseDto.toDto(userCourse.getCourse()))
                .tran(TranResponseDto.toDTO(userCourse.getTran()))
                .enrolledAt(userCourse.getEnrolledAt())
                .expiredAt(userCourse.getExpiredAt())
                .completedAt(userCourse.getCompletedAt())
                .progress(userCourse.getProgress())
                .createdAt(userCourse.getCreatedAt())
                .updatedAt(userCourse.getUpdatedAt())
                .currentLessonId(userCourse.getCurrentLessonId())
                .currentStageId(userCourse.getCurrentStageId())
                .status(userCourse.getStatus())
                .isDeleted(userCourse.getIsDeleted())
                .build();
    }

    public static List<HistoryUserCourse> convertList(List<UserCourse> userCourseList) {
        return userCourseList.stream()
                .map(HistoryUserCourse::toDTO)
                .toList();
    }
}
