package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardOverviewDto implements Serializable {
    private Long totalUsers;
    private Long totalTeachers;
    private Long totalStudents;
    private Long totalCourses;
    private Long activeCourses;
    private Long totalEnrollments;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private Long totalFeedbacks;
    private Double averageRating;
    private Long totalTransactions;
    private Long pendingTransactions;
    private Long completedTransactions;
}

