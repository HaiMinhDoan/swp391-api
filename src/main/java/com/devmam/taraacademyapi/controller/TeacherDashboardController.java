package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.constant.enums.FilterLogicType;
import com.devmam.taraacademyapi.constant.enums.FilterOperation;
import com.devmam.taraacademyapi.constant.enums.SortDirection;
import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.FilterCriteria;
import com.devmam.taraacademyapi.models.dto.request.SortCriteria;
import com.devmam.taraacademyapi.models.dto.response.*;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.Feedback;
import com.devmam.taraacademyapi.models.entities.Lesson;
import com.devmam.taraacademyapi.models.entities.QuizSubmission;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.models.entities.UserCourse;
import com.devmam.taraacademyapi.repository.CourseRepository;
import com.devmam.taraacademyapi.repository.FeedbackRepository;
import com.devmam.taraacademyapi.repository.UserCourseRepository;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.LessonService;
import com.devmam.taraacademyapi.service.impl.entities.QuizSubmissionService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/teacher/dashboard")
@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
public class TeacherDashboardController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private QuizSubmissionService quizSubmissionService;

    /**
     * API 1: Tổng quan thống kê dashboard
     * GET /api/v1/teacher/dashboard/overview
     */
    @GetMapping("/overview")
    public ResponseEntity<ResponseData<TeacherDashboardOverviewDto>> getOverview(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            UUID teacherId = getTeacherId(authHeader);
            
            // Đếm tổng số khóa học
            List<Course> allCourses = courseRepository.findAll().stream()
                    .filter(c -> c.getCreatedBy() != null && c.getCreatedBy().getId().equals(teacherId) && c.getIsDeleted() == 0)
                    .collect(Collectors.toList());
            
            long totalCourses = allCourses.size();
            long activeCourses = allCourses.stream()
                    .filter(c -> c.getStatus() != null && c.getStatus() == 1)
                    .count();
            
            // Lấy danh sách course IDs
            List<Integer> courseIds = allCourses.stream()
                    .map(Course::getId)
                    .collect(Collectors.toList());
            
            // Đếm số học sinh
            List<UserCourse> allUserCourses = userCourseRepository.findAll().stream()
                    .filter(uc -> courseIds.contains(uc.getCourse().getId()) && uc.getIsDeleted() == 0)
                    .collect(Collectors.toList());
            
            long totalStudents = allUserCourses.stream()
                    .map(uc -> uc.getUser().getId())
                    .distinct()
                    .count();
            
            long activeStudents = allUserCourses.stream()
                    .filter(uc -> uc.getExpiredAt() != null && uc.getExpiredAt().isAfter(Instant.now()))
                    .map(uc -> uc.getUser().getId())
                    .distinct()
                    .count();
            
            // Tính doanh thu
            BigDecimal totalRevenue = allUserCourses.stream()
                    .filter(uc -> uc.getTran() != null && uc.getTran().getAmount() != null)
                    .map(uc -> uc.getTran().getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Doanh thu tháng hiện tại
            LocalDate now = LocalDate.now();
            Instant startOfMonth = now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            BigDecimal monthlyRevenue = allUserCourses.stream()
                    .filter(uc -> uc.getTran() != null && uc.getTran().getAmount() != null 
                            && uc.getTran().getCreatedAt() != null 
                            && uc.getTran().getCreatedAt().isAfter(startOfMonth))
                    .map(uc -> uc.getTran().getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Đánh giá trung bình
            List<Feedback> feedbacks = feedbackRepository.findAll().stream()
                    .filter(f -> f.getReferenceType() != null && f.getReferenceType().equals("COURSE")
                            && courseIds.contains(f.getReferenceId()) && f.getIsDeleted() == 0)
                    .collect(Collectors.toList());
            
            double averageRating = feedbacks.stream()
                    .filter(f -> f.getRating() != null)
                    .mapToInt(Feedback::getRating)
                    .average()
                    .orElse(0.0);
            
            long totalFeedbacks = feedbacks.size();
            
            // Số khóa học đã hoàn thành
            long completedCourses = allUserCourses.stream()
                    .filter(uc -> uc.getCompletedAt() != null)
                    .count();
            
            // Tỷ lệ hoàn thành trung bình
            double averageCompletionRate = allUserCourses.stream()
                    .filter(uc -> uc.getProgress() != null)
                    .mapToDouble(uc -> uc.getProgress().doubleValue())
                    .average()
                    .orElse(0.0);
            
            TeacherDashboardOverviewDto overview = TeacherDashboardOverviewDto.builder()
                    .totalCourses(totalCourses)
                    .activeCourses(activeCourses)
                    .totalStudents(totalStudents)
                    .activeStudents(activeStudents)
                    .totalRevenue(totalRevenue)
                    .monthlyRevenue(monthlyRevenue)
                    .averageRating(averageRating)
                    .totalFeedbacks(totalFeedbacks)
                    .completedCourses(completedCourses)
                    .averageCompletionRate(averageCompletionRate)
                    .build();
            
            return ResponseEntity.ok(ResponseData.<TeacherDashboardOverviewDto>builder()
                    .status(200)
                    .message("Dashboard overview retrieved successfully")
                    .error(null)
                    .data(overview)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<TeacherDashboardOverviewDto>builder()
                            .status(500)
                            .message("Failed to get dashboard overview")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * API 2: Danh sách khóa học của giáo viên
     * GET /api/v1/teacher/dashboard/courses
     */
    @GetMapping("/courses")
    public ResponseEntity<ResponseData<Page<CourseResponseDto>>> getCourses(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            UUID teacherId = getTeacherId(authHeader);
            
            Pageable pageable = PageRequest.of(page, size);
            List<Course> allCourses = courseRepository.findAll().stream()
                    .filter(c -> c.getCreatedBy() != null && c.getCreatedBy().getId().equals(teacherId) && c.getIsDeleted() == 0)
                    .collect(Collectors.toList());
            
            // Manual pagination
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allCourses.size());
            List<Course> pagedCourses = allCourses.subList(start, end);
            
            List<CourseResponseDto> courseDtos = pagedCourses.stream()
                    .map(this::toCourseResponseDto)
                    .collect(Collectors.toList());
            
            Page<CourseResponseDto> coursePage = new org.springframework.data.domain.PageImpl<>(
                    courseDtos, pageable, allCourses.size());
            
            return ResponseEntity.ok(ResponseData.<Page<CourseResponseDto>>builder()
                    .status(200)
                    .message("Courses retrieved successfully")
                    .error(null)
                    .data(coursePage)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<Page<CourseResponseDto>>builder()
                            .status(500)
                            .message("Failed to get courses")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * API 3: Danh sách học sinh đang học
     * GET /api/v1/teacher/dashboard/students
     */
    @GetMapping("/students")
    public ResponseEntity<ResponseData<Map<String, Object>>> getStudents(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            UUID teacherId = getTeacherId(authHeader);
            
            // Lấy danh sách course IDs của giáo viên
            final List<Integer> allCourseIds = courseRepository.findAll().stream()
                    .filter(c -> c.getCreatedBy() != null && c.getCreatedBy().getId().equals(teacherId) && c.getIsDeleted() == 0)
                    .map(Course::getId)
                    .collect(Collectors.toList());
            
            final List<Integer> courseIds;
            if (courseId != null) {
                if (!allCourseIds.contains(courseId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ResponseData.<Map<String, Object>>builder()
                                    .status(403)
                                    .message("You don't have access to this course")
                                    .error("Access denied")
                                    .data(null)
                                    .build());
                }
                courseIds = Collections.singletonList(courseId);
            } else {
                courseIds = allCourseIds;
            }
            
            // Lấy danh sách học sinh
            List<UserCourse> allUserCourses = userCourseRepository.findAll().stream()
                    .filter(uc -> courseIds.contains(uc.getCourse().getId()) 
                            && uc.getIsDeleted() == 0
                            && uc.getExpiredAt() != null 
                            && uc.getExpiredAt().isAfter(Instant.now()))
                    .collect(Collectors.toList());
            
            // Filter theo tên nếu có search
            if (search != null && !search.trim().isEmpty()) {
                final String searchLower = search.toLowerCase().trim();
                allUserCourses = allUserCourses.stream()
                        .filter(uc -> {
                            String fullName = uc.getUser().getFullName();
                            String email = uc.getUser().getEmail();
                            String phone = uc.getUser().getPhone();
                            return (fullName != null && fullName.toLowerCase().contains(searchLower))
                                    || (email != null && email.toLowerCase().contains(searchLower))
                                    || (phone != null && phone.contains(search));
                        })
                        .collect(Collectors.toList());
            }
            
            // Tính tổng số records trước khi phân trang
            long totalElements = allUserCourses.size();
            
            // Phân trang
            int start = page * size;
            int end = Math.min(start + size, allUserCourses.size());
            List<UserCourse> pagedUserCourses = start < allUserCourses.size() 
                    ? allUserCourses.subList(start, end)
                    : Collections.emptyList();
            
            // Map sang response
            List<Map<String, Object>> students = pagedUserCourses.stream()
                    .map(uc -> {
                        Map<String, Object> student = new HashMap<>();
                        student.put("userId", uc.getUser().getId());
                        student.put("fullName", uc.getUser().getFullName());
                        student.put("email", uc.getUser().getEmail());
                        student.put("phone", uc.getUser().getPhone());
                        student.put("username", uc.getUser().getUsername());
                        student.put("avatar", uc.getUser().getAvt());
                        student.put("courseId", uc.getCourse().getId());
                        student.put("courseTitle", uc.getCourse().getTitle());
                        student.put("enrolledAt", uc.getEnrolledAt());
                        student.put("expiredAt", uc.getExpiredAt());
                        student.put("progress", uc.getProgress());
                        student.put("completedAt", uc.getCompletedAt());
                        return student;
                    })
                    .collect(Collectors.toList());
            
            // Tạo response với pagination info
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", students);
            responseData.put("totalElements", totalElements);
            responseData.put("totalPages", (int) Math.ceil((double) totalElements / size));
            responseData.put("currentPage", page);
            responseData.put("pageSize", size);
            responseData.put("hasNext", (page + 1) * size < totalElements);
            responseData.put("hasPrevious", page > 0);
            
            return ResponseEntity.ok(ResponseData.<Map<String, Object>>builder()
                    .status(200)
                    .message("Students retrieved successfully")
                    .error(null)
                    .data(responseData)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<Map<String, Object>>builder()
                            .status(500)
                            .message("Failed to get students")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * API: Danh sách học sinh đã đăng ký khóa học của giáo viên (theo userId)
     * GET /api/v1/teacher/dashboard/students/by-teacher
     */
    @GetMapping("/students/by-teacher")
    public ResponseEntity<ResponseData<List<Map<String, Object>>>> getStudentsByTeacherId(
            @RequestParam UUID userId,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false, defaultValue = "false") Boolean includeExpired) {
        try {
            // Kiểm tra user có tồn tại và là giáo viên không
            Optional<User> teacherOpt = userService.getOne(userId);
            if (teacherOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseData.<List<Map<String, Object>>>builder()
                                .status(404)
                                .message("Teacher not found")
                                .error("User with id " + userId + " not found")
                                .data(null)
                                .build());
            }

            User teacher = teacherOpt.get();
            if (teacher.getRole() == null || !teacher.getRole().contains("TEACHER")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseData.<List<Map<String, Object>>>builder()
                                .status(400)
                                .message("User is not a teacher")
                                .error("User with id " + userId + " is not a teacher")
                                .data(null)
                                .build());
            }

            // Lấy danh sách course IDs của giáo viên
            final List<Integer> allCourseIds = courseRepository.findAll().stream()
                    .filter(c -> c.getCreatedBy() != null 
                            && c.getCreatedBy().getId().equals(userId) 
                            && c.getIsDeleted() == 0)
                    .map(Course::getId)
                    .collect(Collectors.toList());

            if (allCourseIds.isEmpty()) {
                return ResponseEntity.ok(ResponseData.<List<Map<String, Object>>>builder()
                        .status(200)
                        .message("No courses found for this teacher")
                        .error(null)
                        .data(Collections.emptyList())
                        .build());
            }

            final List<Integer> courseIds;
            if (courseId != null) {
                if (!allCourseIds.contains(courseId)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseData.<List<Map<String, Object>>>builder()
                                    .status(400)
                                    .message("Course does not belong to this teacher")
                                    .error("Course with id " + courseId + " does not belong to teacher " + userId)
                                    .data(null)
                                    .build());
                }
                courseIds = Collections.singletonList(courseId);
            } else {
                courseIds = allCourseIds;
            }

            // Lấy danh sách học sinh đã đăng ký
            List<UserCourse> userCourses = userCourseRepository.findAll().stream()
                    .filter(uc -> courseIds.contains(uc.getCourse().getId()) 
                            && uc.getIsDeleted() == 0)
                    .filter(uc -> {
                        if (includeExpired) {
                            return true; // Bao gồm cả học sinh đã hết hạn
                        } else {
                            // Chỉ lấy học sinh còn hạn
                            return uc.getExpiredAt() != null && uc.getExpiredAt().isAfter(Instant.now());
                        }
                    })
                    .collect(Collectors.toList());

            // Nhóm theo user để tránh trùng lặp nếu một học sinh đăng ký nhiều khóa học
            Map<UUID, List<UserCourse>> studentsMap = userCourses.stream()
                    .collect(Collectors.groupingBy(uc -> uc.getUser().getId()));

            List<Map<String, Object>> students = new ArrayList<>();
            for (Map.Entry<UUID, List<UserCourse>> entry : studentsMap.entrySet()) {
                UserCourse firstEnrollment = entry.getValue().get(0);
                User user = firstEnrollment.getUser();

                // Tính tổng số khóa học đã đăng ký
                long totalCoursesEnrolled = entry.getValue().size();
                
                // Tính tổng tiến độ trung bình
                double avgProgress = entry.getValue().stream()
                        .filter(uc -> uc.getProgress() != null)
                        .mapToDouble(uc -> uc.getProgress().doubleValue())
                        .average()
                        .orElse(0.0);

                // Lấy danh sách khóa học đã đăng ký
                List<Map<String, Object>> enrolledCourses = entry.getValue().stream()
                        .map(uc -> {
                            Map<String, Object> courseInfo = new HashMap<>();
                            courseInfo.put("courseId", uc.getCourse().getId());
                            courseInfo.put("courseTitle", uc.getCourse().getTitle());
                            courseInfo.put("enrolledAt", uc.getEnrolledAt());
                            courseInfo.put("expiredAt", uc.getExpiredAt());
                            courseInfo.put("progress", uc.getProgress());
                            courseInfo.put("completedAt", uc.getCompletedAt());
                            courseInfo.put("isExpired", uc.getExpiredAt() != null && uc.getExpiredAt().isBefore(Instant.now()));
                            return courseInfo;
                        })
                        .collect(Collectors.toList());

                Map<String, Object> student = new HashMap<>();
                student.put("userId", user.getId());
                student.put("fullName", user.getFullName());
                student.put("email", user.getEmail());
                student.put("phone", user.getPhone());
                student.put("username", user.getUsername());
                student.put("avatar", user.getAvt());
                student.put("totalCoursesEnrolled", totalCoursesEnrolled);
                student.put("averageProgress", avgProgress);
                student.put("firstEnrolledAt", firstEnrollment.getEnrolledAt());
                student.put("enrolledCourses", enrolledCourses);
                students.add(student);
            }

            // Sắp xếp theo ngày đăng ký đầu tiên (mới nhất trước)
            students.sort((s1, s2) -> {
                Instant date1 = (Instant) s1.get("firstEnrolledAt");
                Instant date2 = (Instant) s2.get("firstEnrolledAt");
                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return 1;
                if (date2 == null) return -1;
                return date2.compareTo(date1);
            });

            return ResponseEntity.ok(ResponseData.<List<Map<String, Object>>>builder()
                    .status(200)
                    .message("Students retrieved successfully")
                    .error(null)
                    .data(students)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<Map<String, Object>>>builder()
                            .status(500)
                            .message("Failed to get students")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * API 4: Thống kê doanh thu theo thời gian
     * GET /api/v1/teacher/dashboard/revenue
     */
    @GetMapping("/revenue")
    public ResponseEntity<ResponseData<List<TeacherRevenueDto>>> getRevenue(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String period, // "month" or "year"
            @RequestParam(required = false) Integer months) {
        try {
            UUID teacherId = getTeacherId(authHeader);
            
            final String finalPeriod = period != null ? period : "month";
            final int finalMonths = months != null ? months : 6;
            
            // Lấy danh sách course IDs
            List<Integer> courseIds = courseRepository.findAll().stream()
                    .filter(c -> c.getCreatedBy() != null && c.getCreatedBy().getId().equals(teacherId) && c.getIsDeleted() == 0)
                    .map(Course::getId)
                    .collect(Collectors.toList());
            
            List<UserCourse> userCourses = userCourseRepository.findAll().stream()
                    .filter(uc -> courseIds.contains(uc.getCourse().getId()) 
                            && uc.getTran() != null 
                            && uc.getTran().getAmount() != null
                            && uc.getTran().getCreatedAt() != null)
                    .collect(Collectors.toList());
            
            List<TeacherRevenueDto> revenueData = new ArrayList<>();
            
            if ("month".equals(finalPeriod)) {
                // Group by month
                Map<String, List<UserCourse>> monthlyData = userCourses.stream()
                        .filter(uc -> uc.getTran().getCreatedAt().isAfter(
                                Instant.now().minusSeconds(finalMonths * 30L * 24 * 60 * 60)))
                        .collect(Collectors.groupingBy(uc -> {
                            LocalDate date = uc.getTran().getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                            return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                        }));
                
                monthlyData.forEach((month, courses) -> {
                    BigDecimal revenue = courses.stream()
                            .map(uc -> uc.getTran().getAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    LocalDate monthDate = LocalDate.parse(month + "-01");
                    Instant startDate = monthDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
                    Instant endDate = monthDate.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
                    
                    revenueData.add(TeacherRevenueDto.builder()
                            .period(month)
                            .revenue(revenue)
                            .transactionCount((long) courses.size())
                            .startDate(startDate)
                            .endDate(endDate)
                            .build());
                });
            } else {
                // Group by year
                Map<String, List<UserCourse>> yearlyData = userCourses.stream()
                        .collect(Collectors.groupingBy(uc -> {
                            LocalDate date = uc.getTran().getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                            return String.valueOf(date.getYear());
                        }));
                
                yearlyData.forEach((year, courses) -> {
                    BigDecimal revenue = courses.stream()
                            .map(uc -> uc.getTran().getAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    LocalDate yearDate = LocalDate.of(Integer.parseInt(year), 1, 1);
                    Instant startDate = yearDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
                    Instant endDate = yearDate.plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
                    
                    revenueData.add(TeacherRevenueDto.builder()
                            .period(year)
                            .revenue(revenue)
                            .transactionCount((long) courses.size())
                            .startDate(startDate)
                            .endDate(endDate)
                            .build());
                });
            }
            
            revenueData.sort(Comparator.comparing(TeacherRevenueDto::getPeriod));
            
            return ResponseEntity.ok(ResponseData.<List<TeacherRevenueDto>>builder()
                    .status(200)
                    .message("Revenue data retrieved successfully")
                    .error(null)
                    .data(revenueData)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<TeacherRevenueDto>>builder()
                            .status(500)
                            .message("Failed to get revenue data")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * API 5: Đánh giá từ học sinh
     * GET /api/v1/teacher/dashboard/feedback
     */
    @GetMapping("/feedback")
    public ResponseEntity<ResponseData<List<FeedbackResponseDto>>> getFeedback(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer courseId) {
        try {
            UUID teacherId = getTeacherId(authHeader);
            
            // Lấy danh sách course IDs
            final List<Integer> allCourseIds = courseRepository.findAll().stream()
                    .filter(c -> c.getCreatedBy() != null && c.getCreatedBy().getId().equals(teacherId) && c.getIsDeleted() == 0)
                    .map(Course::getId)
                    .collect(Collectors.toList());
            
            final List<Integer> courseIds;
            if (courseId != null) {
                if (!allCourseIds.contains(courseId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ResponseData.<List<FeedbackResponseDto>>builder()
                                    .status(403)
                                    .message("You don't have access to this course")
                                    .error("Access denied")
                                    .data(null)
                                    .build());
                }
                courseIds = Collections.singletonList(courseId);
            } else {
                courseIds = allCourseIds;
            }
            
            List<Feedback> feedbacks = feedbackRepository.findAll().stream()
                    .filter(f -> f.getReferenceType() != null 
                            && f.getReferenceType().equals("COURSE")
                            && courseIds.contains(f.getReferenceId()) 
                            && f.getIsDeleted() == 0)
                    .sorted(Comparator.comparing(Feedback::getCreatedAt).reversed())
                    .collect(Collectors.toList());
            
            List<FeedbackResponseDto> feedbackDtos = feedbacks.stream()
                    .map(this::toFeedbackResponseDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ResponseData.<List<FeedbackResponseDto>>builder()
                    .status(200)
                    .message("Feedbacks retrieved successfully")
                    .error(null)
                    .data(feedbackDtos)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<FeedbackResponseDto>>builder()
                            .status(500)
                            .message("Failed to get feedbacks")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * API 6: Hiệu suất khóa học
     * GET /api/v1/teacher/dashboard/performance
     */
    @GetMapping("/performance")
    public ResponseEntity<ResponseData<List<TeacherCoursePerformanceDto>>> getPerformance(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            UUID teacherId = getTeacherId(authHeader);
            
            List<Course> courses = courseRepository.findAll().stream()
                    .filter(c -> c.getCreatedBy() != null && c.getCreatedBy().getId().equals(teacherId) && c.getIsDeleted() == 0)
                    .collect(Collectors.toList());
            
            List<TeacherCoursePerformanceDto> performanceList = new ArrayList<>();
            
            for (Course course : courses) {
                List<UserCourse> userCourses = userCourseRepository.findAll().stream()
                        .filter(uc -> uc.getCourse().getId().equals(course.getId()) && uc.getIsDeleted() == 0)
                        .collect(Collectors.toList());
                
                long enrolledStudents = userCourses.size();
                long completedStudents = userCourses.stream()
                        .filter(uc -> uc.getCompletedAt() != null)
                        .count();
                
                double completionRate = enrolledStudents > 0 
                        ? (completedStudents * 100.0 / enrolledStudents) 
                        : 0.0;
                
                double averageProgress = userCourses.stream()
                        .filter(uc -> uc.getProgress() != null)
                        .mapToDouble(uc -> uc.getProgress().doubleValue())
                        .average()
                        .orElse(0.0);
                
                // Lấy feedbacks cho khóa học này
                List<Feedback> courseFeedbacks = feedbackRepository.findAll().stream()
                        .filter(f -> f.getReferenceType() != null 
                                && f.getReferenceType().equals("COURSE")
                                && f.getReferenceId().equals(course.getId())
                                && f.getIsDeleted() == 0)
                        .collect(Collectors.toList());
                
                double averageRating = courseFeedbacks.stream()
                        .filter(f -> f.getRating() != null)
                        .mapToInt(Feedback::getRating)
                        .average()
                        .orElse(0.0);
                
                performanceList.add(TeacherCoursePerformanceDto.builder()
                        .courseId(course.getId())
                        .courseTitle(course.getTitle())
                        .enrolledStudents(enrolledStudents)
                        .completedStudents(completedStudents)
                        .completionRate(completionRate)
                        .averageQuizScore(averageProgress) // Using progress as quiz score proxy
                        .averageRating(averageRating)
                        .totalFeedbacks((long) courseFeedbacks.size())
                        .build());
            }
            
            return ResponseEntity.ok(ResponseData.<List<TeacherCoursePerformanceDto>>builder()
                    .status(200)
                    .message("Performance data retrieved successfully")
                    .error(null)
                    .data(performanceList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<List<TeacherCoursePerformanceDto>>builder()
                            .status(500)
                            .message("Failed to get performance data")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    // Helper methods
    private UUID getTeacherId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        String token = jwtService.getTokenFromAuthHeader(authHeader);
        return jwtService.getUserId(token);
    }

    private CourseResponseDto toCourseResponseDto(Course course) {
        return CourseResponseDto.toDto(course);
    }

    private FeedbackResponseDto toFeedbackResponseDto(Feedback feedback) {
        return FeedbackResponseDto.toDTO(feedback);
    }

    // ==================== QUIZ SUBMISSIONS MANAGEMENT ====================

    /**
     * Lấy danh sách bài nộp của học sinh theo lesson
     * GET /api/v1/teacher/dashboard/lessons/{lessonId}/submissions
     */
    @GetMapping("/lessons/{lessonId}/submissions")
    public ResponseEntity<ResponseData<Page<QuizSubmissionResponseDto>>> getLessonSubmissions(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Integer lessonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir) {
        try {
            UUID teacherId = getTeacherId(authHeader);
            
            // Kiểm tra lesson tồn tại và thuộc course của giáo viên
            Lesson lesson = lessonService.getOne(lessonId).orElseThrow(() -> 
                    new RuntimeException("Lesson not found"));
            
            if (lesson.getStage() == null || lesson.getStage().getCourse() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseData.<Page<QuizSubmissionResponseDto>>builder()
                                .status(400)
                                .message("Lesson does not belong to any course")
                                .error("Invalid lesson")
                                .data(null)
                                .build());
            }
            
            Course course = lesson.getStage().getCourse();
            
            // Kiểm tra quyền sở hữu
            if (course.getCreatedBy() == null || 
                    !course.getCreatedBy().getId().equals(teacherId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ResponseData.<Page<QuizSubmissionResponseDto>>builder()
                                .status(403)
                                .message("You don't have permission to view submissions for this lesson")
                                .error("Access denied")
                                .data(null)
                                .build());
            }

            // Tạo filter request để lấy quiz submissions theo lessonId
            BaseFilterRequest filterRequest = new BaseFilterRequest();
            
            // Tạo filter cho lesson.id
            FilterCriteria lessonFilter = new FilterCriteria();
            lessonFilter.setFieldName("lesson.id");
            lessonFilter.setOperation(FilterOperation.EQUALS);
            lessonFilter.setValue(lessonId.toString());
            lessonFilter.setLogicType(FilterLogicType.AND);
            
            filterRequest.setFilters(List.of(lessonFilter));
            
            // Thêm sort nếu có
            if (sortBy != null && !sortBy.isEmpty()) {
                SortCriteria sortCriteria = new SortCriteria();
                sortCriteria.setFieldName(sortBy);
                sortCriteria.setDirection(sortDir != null && sortDir.equalsIgnoreCase("ASC") 
                        ? SortDirection.ASC 
                        : SortDirection.DESC);
                filterRequest.setSorts(List.of(sortCriteria));
            } else {
                // Mặc định sort theo createdAt DESC
                SortCriteria defaultSort = new SortCriteria();
                defaultSort.setFieldName("createdAt");
                defaultSort.setDirection(SortDirection.DESC);
                filterRequest.setSorts(List.of(defaultSort));
            }
            
            filterRequest.setPage(page);
            filterRequest.setSize(size);

            // Lấy danh sách quiz submissions
            Page<QuizSubmission> submissionsPage = quizSubmissionService.filter(filterRequest);
            Page<QuizSubmissionResponseDto> responsePage = submissionsPage.map(QuizSubmissionResponseDto::toDTO);

            return ResponseEntity.ok(ResponseData.<Page<QuizSubmissionResponseDto>>builder()
                    .status(200)
                    .message("Quiz submissions retrieved successfully")
                    .error(null)
                    .data(responsePage)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in getLessonSubmissions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<Page<QuizSubmissionResponseDto>>builder()
                            .status(500)
                            .message("Failed to get quiz submissions")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }
}

