package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDashboardOverviewDto implements Serializable {
    private Long totalCourses;
    private Long activeCourses;
    private Long totalStudents;
    private Long activeStudents;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private Double averageRating;
    private Long totalFeedbacks;
    private Long completedCourses;
    private Double averageCompletionRate;
}

