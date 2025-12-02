package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherCoursePerformanceDto implements Serializable {
    private Integer courseId;
    private String courseTitle;
    private Long enrolledStudents;
    private Long completedStudents;
    private Double completionRate;
    private Double averageQuizScore;
    private Double averageRating;
    private Long totalFeedbacks;
}

