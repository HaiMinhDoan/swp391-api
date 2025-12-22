package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseAdviceDto {

    String greeting; // Lời chào khách hàng
    String analysis; // Phân tích nhu cầu khách hàng
    List<RecommendedCourse> recommendedCourses; // Danh sách khóa học đề xuất
    String priceAdvice; // Tư vấn về giá
    String conclusion; // Kết luận và lời khuyên
    String nextSteps; // Các bước tiếp theo cho khách hàng

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecommendedCourse {
        Integer courseId;
        String courseName;
        String reason; // Lý do đề xuất
        BigDecimal originalPrice;
        Integer saleOff;
        BigDecimal finalPrice;
        String category;
        Integer priorityLevel; // 1 = cao nhất, 2 = trung bình, 3 = thấp
    }
}