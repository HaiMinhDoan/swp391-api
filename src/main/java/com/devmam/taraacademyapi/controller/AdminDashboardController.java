package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.response.*;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.Tran;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.models.entities.UserCourse;
import com.devmam.taraacademyapi.repository.CourseRepository;
import com.devmam.taraacademyapi.repository.TranRepository;
import com.devmam.taraacademyapi.repository.UserCourseRepository;
import com.devmam.taraacademyapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private TranRepository tranRepository;

    /**
     * API 1: Tổng quan thống kê dashboard
     * GET /api/v1/admin/dashboard/overview
     */
    @GetMapping("/overview")
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseData<AdminDashboardOverviewDto>> getOverview() {
        try {
            // Lấy users với null safety
            List<User> allUsers = new ArrayList<>();
            try {
                allUsers = userRepository.findAll().stream()
                        .filter(u -> u != null && u.getIsDeleted() != null && u.getIsDeleted() == 0)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Error fetching users: " + e.getMessage());
                e.printStackTrace();
            }

            long totalUsers = allUsers.size();
            long totalTeachers = allUsers.stream()
                    .filter(u -> u.getRole() != null && u.getRole().contains("TEACHER"))
                    .count();
            long totalStudents = allUsers.stream()
                    .filter(u -> u.getRole() != null && u.getRole().contains("STUDENT"))
                    .count();

            // Lấy courses với null safety
            List<Course> allCourses = new ArrayList<>();
            try {
                allCourses = courseRepository.findAll().stream()
                        .filter(c -> c != null && c.getIsDeleted() != null && c.getIsDeleted() == 0)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Error fetching courses: " + e.getMessage());
                e.printStackTrace();
            }

            long totalCourses = allCourses.size();
            long activeCourses = allCourses.stream()
                    .filter(c -> c.getStatus() != null && c.getStatus() == 1)
                    .count();

            // Lấy enrollments với null safety
            List<UserCourse> allUserCourses = new ArrayList<>();
            try {
                allUserCourses = userCourseRepository.findAll().stream()
                        .filter(uc -> uc != null && uc.getIsDeleted() != null && uc.getIsDeleted() == 0)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Error fetching user courses: " + e.getMessage());
                e.printStackTrace();
            }

            long totalEnrollments = allUserCourses.size();

            // Tính doanh thu với null safety
            List<Tran> allTransactions = new ArrayList<>();
            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal monthlyRevenue = BigDecimal.ZERO;
            long totalTransactions = 0;
            long pendingTransactions = 0;
            long completedTransactions = 0;

            try {
                allTransactions = tranRepository.findAll().stream()
                        .filter(t -> t != null 
                                && t.getIsDeleted() != null 
                                && t.getIsDeleted() == 0 
                                && t.getAmount() != null)
                        .collect(Collectors.toList());

                totalRevenue = allTransactions.stream()
                        .map(Tran::getAmount)
                        .filter(amount -> amount != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Doanh thu tháng hiện tại
                LocalDate now = LocalDate.now();
                Instant startOfMonth = now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
                monthlyRevenue = allTransactions.stream()
                        .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().isAfter(startOfMonth))
                        .map(Tran::getAmount)
                        .filter(amount -> amount != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                totalTransactions = allTransactions.size();
                pendingTransactions = allTransactions.stream()
                        .filter(t -> t.getStatus() != null && t.getStatus() == 0)
                        .count();
                completedTransactions = allTransactions.stream()
                        .filter(t -> t.getStatus() != null && t.getStatus() == 1)
                        .count();
            } catch (Exception e) {
                System.err.println("Error calculating revenue: " + e.getMessage());
                e.printStackTrace();
            }

            // Đánh giá - để mặc định 0
            long totalFeedbacks = 0L;
            double averageRating = 0.0;

            AdminDashboardOverviewDto overview = AdminDashboardOverviewDto.builder()
                    .totalUsers(totalUsers)
                    .totalTeachers(totalTeachers)
                    .totalStudents(totalStudents)
                    .totalCourses(totalCourses)
                    .activeCourses(activeCourses)
                    .totalEnrollments(totalEnrollments)
                    .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                    .monthlyRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO)
                    .totalFeedbacks(totalFeedbacks)
                    .averageRating(averageRating)
                    .totalTransactions(totalTransactions)
                    .pendingTransactions(pendingTransactions)
                    .completedTransactions(completedTransactions)
                    .build();

            return ResponseEntity.ok(ResponseData.<AdminDashboardOverviewDto>builder()
                    .status(200)
                    .message("Dashboard overview retrieved successfully")
                    .error(null)
                    .data(overview)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in getOverview: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<AdminDashboardOverviewDto>builder()
                            .status(500)
                            .message("Failed to get dashboard overview")
                            .error(e.getMessage() + (e.getCause() != null ? " - Caused by: " + e.getCause().getMessage() : ""))
                            .data(null)
                            .build());
        }
    }

    /**
     * API 2: Line Chart - Doanh thu theo thời gian
     * GET /api/v1/admin/dashboard/charts/revenue-line
     */
    @GetMapping("/charts/revenue-line")
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseData<List<AdminLineChartDataDto>>> getRevenueLineChart(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(defaultValue = "12") Integer months) {
        try {
            List<Tran> transactions = tranRepository.findAll().stream()
                    .filter(t -> t.getIsDeleted() == 0 
                            && t.getAmount() != null 
                            && t.getCreatedAt() != null
                            && t.getCreatedAt().isAfter(Instant.now().minusSeconds(months * 30L * 24 * 60 * 60)))
                    .collect(Collectors.toList());

            List<AdminLineChartDataDto> chartData = new ArrayList<>();

            if ("month".equals(period)) {
                Map<String, List<Tran>> monthlyData = transactions.stream()
                        .collect(Collectors.groupingBy(t -> {
                            LocalDate date = t.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                            return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                        }));

                monthlyData.forEach((month, trans) -> {
                    BigDecimal total = trans.stream()
                            .map(Tran::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    LocalDate monthDate = LocalDate.parse(month + "-01");
                    Instant date = monthDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

                    chartData.add(AdminLineChartDataDto.builder()
                            .label(month)
                            .value((long) trans.size())
                            .amount(total)
                            .date(date)
                            .build());
                });
            } else if ("week".equals(period)) {
                Map<String, List<Tran>> weeklyData = transactions.stream()
                        .collect(Collectors.groupingBy(t -> {
                            LocalDate date = t.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                            int week = date.getDayOfYear() / 7;
                            return date.getYear() + "-W" + String.format("%02d", week);
                        }));

                weeklyData.forEach((week, trans) -> {
                    BigDecimal total = trans.stream()
                            .map(Tran::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    chartData.add(AdminLineChartDataDto.builder()
                            .label(week)
                            .value((long) trans.size())
                            .amount(total)
                            .date(trans.get(0).getCreatedAt())
                            .build());
                });
            } else { // day
                Map<String, List<Tran>> dailyData = transactions.stream()
                        .collect(Collectors.groupingBy(t -> {
                            LocalDate date = t.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                            return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        }));

                dailyData.forEach((day, trans) -> {
                    BigDecimal total = trans.stream()
                            .map(Tran::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    LocalDate dayDate = LocalDate.parse(day);
                    Instant date = dayDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

                    chartData.add(AdminLineChartDataDto.builder()
                            .label(day)
                            .value((long) trans.size())
                            .amount(total)
                            .date(date)
                            .build());
                });
            }

            chartData.sort(Comparator.comparing(AdminLineChartDataDto::getDate));

            return ResponseEntity.ok(ResponseData.<List<AdminLineChartDataDto>>builder()
                    .status(200)
                    .message("Revenue line chart data retrieved successfully")
                    .error(null)
                    .data(chartData)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<AdminLineChartDataDto>>builder()
                            .status(500)
                            .message("Failed to get revenue line chart data")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * API 3: Line Chart - Số user đăng ký theo thời gian
     * GET /api/v1/admin/dashboard/charts/users-line
     */
    @GetMapping("/charts/users-line")
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseData<List<AdminLineChartDataDto>>> getUsersLineChart(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(defaultValue = "12") Integer months) {
        try {
            List<User> users = new ArrayList<>();
            try {
                Instant cutoffDate = Instant.now().minusSeconds(months * 30L * 24 * 60 * 60);
                users = userRepository.findAll().stream()
                        .filter(u -> u != null 
                                && u.getIsDeleted() != null 
                                && u.getIsDeleted() == 0 
                                && u.getCreatedAt() != null
                                && u.getCreatedAt().isAfter(cutoffDate))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Error fetching users: " + e.getMessage());
                e.printStackTrace();
            }

            List<AdminLineChartDataDto> chartData = new ArrayList<>();

            if ("month".equals(period)) {
                try {
                    Map<String, List<User>> monthlyData = users.stream()
                            .filter(u -> u.getCreatedAt() != null)
                            .collect(Collectors.groupingBy(u -> {
                                try {
                                    LocalDate date = u.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                                    return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                                } catch (Exception e) {
                                    System.err.println("Error formatting date for user: " + e.getMessage());
                                    return "unknown";
                                }
                            }));

                    monthlyData.forEach((month, userList) -> {
                        try {
                            if (!"unknown".equals(month) && !userList.isEmpty()) {
                                LocalDate monthDate = LocalDate.parse(month + "-01");
                                Instant date = monthDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

                                chartData.add(AdminLineChartDataDto.builder()
                                        .label(month)
                                        .value((long) userList.size())
                                        .amount(BigDecimal.ZERO)
                                        .date(date)
                                        .build());
                            }
                        } catch (Exception e) {
                            System.err.println("Error processing month " + month + ": " + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error grouping by month: " + e.getMessage());
                    e.printStackTrace();
                }
            } else { // day
                try {
                    Map<String, List<User>> dailyData = users.stream()
                            .filter(u -> u.getCreatedAt() != null)
                            .collect(Collectors.groupingBy(u -> {
                                try {
                                    LocalDate date = u.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                                    return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                } catch (Exception e) {
                                    System.err.println("Error formatting date for user: " + e.getMessage());
                                    return "unknown";
                                }
                            }));

                    dailyData.forEach((day, userList) -> {
                        try {
                            if (!"unknown".equals(day) && !userList.isEmpty()) {
                                LocalDate dayDate = LocalDate.parse(day);
                                Instant date = dayDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

                                chartData.add(AdminLineChartDataDto.builder()
                                        .label(day)
                                        .value((long) userList.size())
                                        .amount(BigDecimal.ZERO)
                                        .date(date)
                                        .build());
                            }
                        } catch (Exception e) {
                            System.err.println("Error processing day " + day + ": " + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error grouping by day: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            chartData.sort(Comparator.comparing(AdminLineChartDataDto::getDate));

            return ResponseEntity.ok(ResponseData.<List<AdminLineChartDataDto>>builder()
                    .status(200)
                    .message("Users line chart data retrieved successfully")
                    .error(null)
                    .data(chartData)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in getUsersLineChart: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<AdminLineChartDataDto>>builder()
                            .status(500)
                            .message("Failed to get users line chart data")
                            .error(e.getMessage() + (e.getCause() != null ? " - Caused by: " + e.getCause().getMessage() : ""))
                            .data(null)
                            .build());
        }
    }

    /**
     * API 4: Line Chart - Số khóa học tạo theo thời gian
     * GET /api/v1/admin/dashboard/charts/courses-line
     */
    @GetMapping("/charts/courses-line")
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseData<List<AdminLineChartDataDto>>> getCoursesLineChart(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(defaultValue = "12") Integer months) {
        try {
            List<Course> courses = courseRepository.findAll().stream()
                    .filter(c -> c.getIsDeleted() == 0 
                            && c.getCreatedAt() != null
                            && c.getCreatedAt().isAfter(Instant.now().minusSeconds(months * 30L * 24 * 60 * 60)))
                    .collect(Collectors.toList());

            List<AdminLineChartDataDto> chartData = new ArrayList<>();

            if ("month".equals(period)) {
                Map<String, List<Course>> monthlyData = courses.stream()
                        .collect(Collectors.groupingBy(c -> {
                            LocalDate date = c.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                            return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                        }));

                monthlyData.forEach((month, courseList) -> {
                    LocalDate monthDate = LocalDate.parse(month + "-01");
                    Instant date = monthDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

                    chartData.add(AdminLineChartDataDto.builder()
                            .label(month)
                            .value((long) courseList.size())
                            .amount(BigDecimal.ZERO)
                            .date(date)
                            .build());
                });
            } else { // day
                Map<String, List<Course>> dailyData = courses.stream()
                        .collect(Collectors.groupingBy(c -> {
                            LocalDate date = c.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                            return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        }));

                dailyData.forEach((day, courseList) -> {
                    LocalDate dayDate = LocalDate.parse(day);
                    Instant date = dayDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

                    chartData.add(AdminLineChartDataDto.builder()
                            .label(day)
                            .value((long) courseList.size())
                            .amount(BigDecimal.ZERO)
                            .date(date)
                            .build());
                });
            }

            chartData.sort(Comparator.comparing(AdminLineChartDataDto::getDate));

            return ResponseEntity.ok(ResponseData.<List<AdminLineChartDataDto>>builder()
                    .status(200)
                    .message("Courses line chart data retrieved successfully")
                    .error(null)
                    .data(chartData)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<AdminLineChartDataDto>>builder()
                            .status(500)
                            .message("Failed to get courses line chart data")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * API 5: Pie Chart - Phân bổ user theo role
     * GET /api/v1/admin/dashboard/charts/users-pie
     */
    @GetMapping("/charts/users-pie")
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseData<List<AdminPieChartDataDto>>> getUsersPieChart() {
        try {
            List<User> users = new ArrayList<>();
            try {
                users = userRepository.findAll().stream()
                        .filter(u -> u != null && u.getIsDeleted() != null && u.getIsDeleted() == 0)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Error fetching users: " + e.getMessage());
                e.printStackTrace();
            }

            long totalUsers = users.size();

            Map<String, Long> roleCount = new HashMap<>();
            try {
                roleCount = users.stream()
                        .collect(Collectors.groupingBy(
                                u -> {
                                    try {
                                        String role = u.getRole();
                                        if (role == null || role.isEmpty()) return "Unknown";
                                        if (role.contains("ADMIN")) return "Admin";
                                        if (role.contains("TEACHER")) return "Teacher";
                                        if (role.contains("STUDENT")) return "Student";
                                        return "Other";
                                    } catch (Exception e) {
                                        System.err.println("Error processing user role: " + e.getMessage());
                                        return "Unknown";
                                    }
                                },
                                Collectors.counting()
                        ));
            } catch (Exception e) {
                System.err.println("Error grouping by role: " + e.getMessage());
                e.printStackTrace();
            }

            List<AdminPieChartDataDto> chartData = new ArrayList<>();
            String[] colors = {"#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40"};

            int colorIndex = 0;
            for (Map.Entry<String, Long> entry : roleCount.entrySet()) {
                try {
                    double percentage = totalUsers > 0 ? (entry.getValue() * 100.0 / totalUsers) : 0.0;
                    chartData.add(AdminPieChartDataDto.builder()
                            .name(entry.getKey())
                            .value(entry.getValue())
                            .amount(BigDecimal.ZERO)
                            .color(colors[colorIndex % colors.length])
                            .percentage(percentage)
                            .build());
                    colorIndex++;
                } catch (Exception e) {
                    System.err.println("Error building pie chart data for " + entry.getKey() + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok(ResponseData.<List<AdminPieChartDataDto>>builder()
                    .status(200)
                    .message("Users pie chart data retrieved successfully")
                    .error(null)
                    .data(chartData)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in getUsersPieChart: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<AdminPieChartDataDto>>builder()
                            .status(500)
                            .message("Failed to get users pie chart data")
                            .error(e.getMessage() + (e.getCause() != null ? " - Caused by: " + e.getCause().getMessage() : ""))
                            .data(null)
                            .build());
        }
    }

    /**
     * API 6: Pie Chart - Phân bổ khóa học theo category
     * GET /api/v1/admin/dashboard/charts/courses-pie
     */
    @GetMapping("/charts/courses-pie")
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseData<List<AdminPieChartDataDto>>> getCoursesPieChart() {
        try {
            List<Course> courses = new ArrayList<>();
            try {
                courses = courseRepository.findAll().stream()
                        .filter(c -> c != null && c.getIsDeleted() != null && c.getIsDeleted() == 0)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Error fetching courses: " + e.getMessage());
                e.printStackTrace();
            }

            long totalCourses = courses.size();

            Map<String, Long> categoryCount = new HashMap<>();
            try {
                categoryCount = courses.stream()
                        .collect(Collectors.groupingBy(
                                c -> {
                                    try {
                                        if (c.getCategory() != null && c.getCategory().getName() != null) {
                                            return c.getCategory().getName();
                                        }
                                        return "Uncategorized";
                                    } catch (Exception e) {
                                        System.err.println("Error getting category name: " + e.getMessage());
                                        return "Uncategorized";
                                    }
                                },
                                Collectors.counting()
                        ));
            } catch (Exception e) {
                System.err.println("Error grouping by category: " + e.getMessage());
                e.printStackTrace();
            }

            List<AdminPieChartDataDto> chartData = new ArrayList<>();
            String[] colors = {"#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40", "#FF6384", "#C9CBCF"};

            int colorIndex = 0;
            for (Map.Entry<String, Long> entry : categoryCount.entrySet()) {
                try {
                    double percentage = totalCourses > 0 ? (entry.getValue() * 100.0 / totalCourses) : 0.0;
                    chartData.add(AdminPieChartDataDto.builder()
                            .name(entry.getKey())
                            .value(entry.getValue())
                            .amount(BigDecimal.ZERO)
                            .color(colors[colorIndex % colors.length])
                            .percentage(percentage)
                            .build());
                    colorIndex++;
                } catch (Exception e) {
                    System.err.println("Error building pie chart data for " + entry.getKey() + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok(ResponseData.<List<AdminPieChartDataDto>>builder()
                    .status(200)
                    .message("Courses pie chart data retrieved successfully")
                    .error(null)
                    .data(chartData)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in getCoursesPieChart: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<AdminPieChartDataDto>>builder()
                            .status(500)
                            .message("Failed to get courses pie chart data")
                            .error(e.getMessage() + (e.getCause() != null ? " - Caused by: " + e.getCause().getMessage() : ""))
                            .data(null)
                            .build());
        }
    }

    /**
     * API 7: Pie Chart - Phân bổ doanh thu theo category
     * GET /api/v1/admin/dashboard/charts/revenue-pie
     */
    @GetMapping("/charts/revenue-pie")
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseData<List<AdminPieChartDataDto>>> getRevenuePieChart() {
        try {
            List<UserCourse> userCourses = new ArrayList<>();
            try {
                userCourses = userCourseRepository.findAll().stream()
                        .filter(uc -> uc != null 
                                && uc.getIsDeleted() != null 
                                && uc.getIsDeleted() == 0 
                                && uc.getTran() != null 
                                && uc.getTran().getAmount() != null)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Error fetching user courses: " + e.getMessage());
                e.printStackTrace();
            }

            Map<String, List<UserCourse>> categoryRevenue = new HashMap<>();
            try {
                categoryRevenue = userCourses.stream()
                        .collect(Collectors.groupingBy(
                                uc -> {
                                    try {
                                        if (uc.getCourse() != null 
                                                && uc.getCourse().getCategory() != null 
                                                && uc.getCourse().getCategory().getName() != null) {
                                            return uc.getCourse().getCategory().getName();
                                        }
                                        return "Uncategorized";
                                    } catch (Exception e) {
                                        System.err.println("Error getting category name: " + e.getMessage());
                                        return "Uncategorized";
                                    }
                                }
                        ));
            } catch (Exception e) {
                System.err.println("Error grouping by category: " + e.getMessage());
                e.printStackTrace();
            }

            BigDecimal totalRevenue = BigDecimal.ZERO;
            try {
                totalRevenue = userCourses.stream()
                        .filter(uc -> uc.getTran() != null && uc.getTran().getAmount() != null)
                        .map(uc -> uc.getTran().getAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            } catch (Exception e) {
                System.err.println("Error calculating total revenue: " + e.getMessage());
                e.printStackTrace();
            }

            List<AdminPieChartDataDto> chartData = new ArrayList<>();
            String[] colors = {"#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40", "#FF6384", "#C9CBCF"};

            int colorIndex = 0;
            for (Map.Entry<String, List<UserCourse>> entry : categoryRevenue.entrySet()) {
                try {
                    BigDecimal categoryTotal = entry.getValue().stream()
                            .filter(uc -> uc.getTran() != null && uc.getTran().getAmount() != null)
                            .map(uc -> uc.getTran().getAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    double percentage = totalRevenue.compareTo(BigDecimal.ZERO) > 0 
                            ? (categoryTotal.doubleValue() * 100.0 / totalRevenue.doubleValue()) 
                            : 0.0;

                    chartData.add(AdminPieChartDataDto.builder()
                            .name(entry.getKey())
                            .value((long) entry.getValue().size())
                            .amount(categoryTotal)
                            .color(colors[colorIndex % colors.length])
                            .percentage(percentage)
                            .build());
                    colorIndex++;
                } catch (Exception e) {
                    System.err.println("Error building pie chart data for " + entry.getKey() + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok(ResponseData.<List<AdminPieChartDataDto>>builder()
                    .status(200)
                    .message("Revenue pie chart data retrieved successfully")
                    .error(null)
                    .data(chartData)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in getRevenuePieChart: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<AdminPieChartDataDto>>builder()
                            .status(500)
                            .message("Failed to get revenue pie chart data")
                            .error(e.getMessage() + (e.getCause() != null ? " - Caused by: " + e.getCause().getMessage() : ""))
                            .data(null)
                            .build());
        }
    }
}

