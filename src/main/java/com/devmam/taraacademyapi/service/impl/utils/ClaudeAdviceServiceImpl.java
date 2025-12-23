package com.devmam.taraacademyapi.service.impl.utils;

import com.devmam.taraacademyapi.models.dto.request.ClaudeAdviceRequest;
import com.devmam.taraacademyapi.models.dto.response.ChatDto;
import com.devmam.taraacademyapi.models.dto.response.ClaudeAdviceResponse;
import com.devmam.taraacademyapi.models.dto.response.CourseAdviceDto;
import com.devmam.taraacademyapi.models.dto.response.CourseResponseDto;
import com.devmam.taraacademyapi.service.ClaudeAdviceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
public class ClaudeAdviceServiceImpl implements ClaudeAdviceService {
    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.url:https://api.anthropic.com/v1/messages}")
    private String apiUrl;

    @Value("${claude.api.version:2023-06-01}")
    private String apiVersion;

    @Value("${claude.model:claude-sonnet-4-20250514}")
    private String model;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ClaudeAdviceServiceImpl(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Tạo prompt hệ thống cho Claude để tư vấn khóa học
     */
    private String createSystemPrompt(List<CourseResponseDto> availableCourses) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Bạn là một chuyên viên tư vấn khóa học chuyên nghiệp và thân thiện tại Tara Academy. ");
        prompt.append("Nhiệm vụ của bạn là phân tích nhu cầu của khách hàng và đề xuất các khóa học phù hợp nhất.\n\n");

        prompt.append("DANH SÁCH CÁC KHÓA HỌC HIỆN CÓ:\n");
        for (CourseResponseDto course : availableCourses) {
            BigDecimal finalPrice = calculateFinalPrice(course.getPrice(), course.getSaleOff());
            prompt.append(String.format(
                    "- ID: %d | Tên: %s | Danh mục: %s | Giá gốc: %s VNĐ | Giảm giá: %d%% | Giá cuối: %s VNĐ | Mô tả: %s\n",
                    course.getId(),
                    course.getTitle(),
                    course.getCategoryName(),
                    formatPrice(course.getPrice()),
                    course.getSaleOff() != null ? course.getSaleOff() : 0,
                    formatPrice(finalPrice),
                    course.getSummary() != null ? course.getSummary() : ""
            ));
        }

        prompt.append("\nYÊU CẦU TRẢ VỀ JSON:\n");
        prompt.append("Bạn PHẢI trả về kết quả dưới dạng JSON với cấu trúc sau (không thêm markdown, không thêm text ngoài JSON):\n");
        prompt.append("{\n");
        prompt.append("  \"greeting\": \"Lời chào thân thiện với khách hàng, giới thiệu mình là chat bot chứ không phải nhân viên tư vấn người thật\",\n");
        prompt.append("  \"analysis\": \"Phân tích ngắn gọn về nhu cầu và mục tiêu của khách hàng\",\n");
        prompt.append("  \"recommendedCourses\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"courseId\": 123,\n");
        prompt.append("      \"courseName\": \"Tên khóa học\",\n");
        prompt.append("      \"reason\": \"Lý do đề xuất khóa học này cho khách hàng\",\n");
        prompt.append("      \"originalPrice\": 1000000,\n");
        prompt.append("      \"saleOff\": 20,\n");
        prompt.append("      \"finalPrice\": 800000,\n");
        prompt.append("      \"category\": \"Tên danh mục\",\n");
        prompt.append("      \"priorityLevel\": 1\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"priceAdvice\": \"Tư vấn về giá cả, giảm giá, giá trị nhận được\",\n");
        prompt.append("  \"conclusion\": \"Kết luận và khuyến nghị tổng thể\",\n");
        prompt.append("  \"nextSteps\": \"Hướng dẫn các bước tiếp theo để đăng ký\"\n");
        prompt.append("}\n\n");

        prompt.append("LƯU Ý:\n");
        prompt.append("- Chỉ đề xuất tối đa 3 khóa học phù hợp nhất\n");
        prompt.append("- priorityLevel: 1 = đề xuất mạnh nhất, 2 = phù hợp, 3 = có thể xem xét\n");
        prompt.append("- Giải thích rõ ràng tại sao khóa học phù hợp với nhu cầu của khách\n");
        prompt.append("- Nhấn mạnh giá trị và lợi ích mà khách hàng nhận được\n");
        prompt.append("- Nếu có giảm giá, hãy nhấn mạnh để tạo động lực\n");
        prompt.append("- Giữ tông giọng thân thiện, chuyên nghiệp và tạo động lực\n");
        prompt.append("- QUAN TRỌNG: Chỉ trả về JSON thuần túy, không thêm bất kỳ text nào khác, nếu tin nhắn cuối của khách hàng hỏi những thứ ngoài lề hãy trả lời lịch sự TỪ CHỐI vì không đúng mục đích\n");

        return prompt.toString();
    }

    /**
     * Tạo prompt từ lịch sử chat của khách hàng
     */
    private String createUserPrompt(ChatDto chat) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("THÔNG TIN KHÁCH HÀNG:\n");

        if (chat.getUser() != null) {
            prompt.append(String.format("- Tên: %s\n", chat.getUser().getFullName()));
            if (chat.getUser().getEmail() != null) {
                prompt.append(String.format("- Email: %s\n", chat.getUser().getEmail()));
            }
        }

        prompt.append("\nLỊCH SỬ HỘI THOẠI:\n");
        if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
            chat.getMessages().forEach(msg -> {
                String sender = Boolean.TRUE.equals(msg.getIsFromUser()) ? "Khách hàng" : "Tư vấn viên";
                prompt.append(String.format("[%s]: %s\n", sender, msg.getContent()));
            });
        }

        prompt.append("\nHãy phân tích cuộc hội thoại trên và đưa ra tư vấn khóa học phù hợp nhất cho khách hàng này.");

        return prompt.toString();
    }

    /**
     * Gọi Claude API để nhận tư vấn
     */
    public CourseAdviceDto getAdviceForChat(ChatDto chat, List<CourseResponseDto> availableCourses) {
        try {
            // Tạo system prompt
            String systemPrompt = createSystemPrompt(availableCourses);

            // Tạo user prompt
            String userPrompt = createUserPrompt(chat);

            // Tạo request
            ClaudeAdviceRequest request = ClaudeAdviceRequest.builder()
                    .model(model)
                    .max_tokens(4096)
                    .temperature(0.7)
                    .messages(List.of(
                            new ClaudeAdviceRequest.Message("user", systemPrompt + "\n\n" + userPrompt)
                    ))
                    .build();

            log.info("Sending request to Claude API for chat ID: {}", chat.getId());

            // Gọi API
            ClaudeAdviceResponse response = webClient.post()
                    .uri(apiUrl)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", apiVersion)
                    .header("content-type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Claude API error: {}", errorBody);
                                        return Mono.error(new RuntimeException("Claude API error: " + errorBody));
                                    })
                    )
                    .bodyToMono(ClaudeAdviceResponse.class)
                    .block();

            if (response != null && response.getResponseText() != null) {
                String jsonResponse = response.getResponseText().trim();

                // Loại bỏ markdown code block nếu có
                if (jsonResponse.startsWith("```json")) {
                    jsonResponse = jsonResponse.substring(7);
                }
                if (jsonResponse.startsWith("```")) {
                    jsonResponse = jsonResponse.substring(3);
                }
                if (jsonResponse.endsWith("```")) {
                    jsonResponse = jsonResponse.substring(0, jsonResponse.length() - 3);
                }
                jsonResponse = jsonResponse.trim();

                log.info("Received response from Claude: {}", jsonResponse);

                // Parse JSON thành DTO
                return objectMapper.readValue(jsonResponse, CourseAdviceDto.class);
            }

            throw new RuntimeException("No response from Claude API");

        } catch (JsonProcessingException e) {
            log.error("Error parsing Claude response to JSON", e);
            throw new RuntimeException("Error parsing Claude response", e);
        } catch (Exception e) {
            log.error("Error calling Claude API", e);
            throw new RuntimeException("Error calling Claude API", e);
        }
    }

    /**
     * Tính giá cuối cùng sau giảm giá
     */
    private BigDecimal calculateFinalPrice(BigDecimal originalPrice, Integer saleOff) {
        if (originalPrice == null) return BigDecimal.ZERO;
        if (saleOff == null || saleOff == 0) return originalPrice;

        BigDecimal discount = originalPrice
                .multiply(BigDecimal.valueOf(saleOff))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return originalPrice.subtract(discount);
    }

    /**
     * Format giá tiền
     */
    private String formatPrice(BigDecimal price) {
        if (price == null) return "0";
        return String.format("%,.0f", price);
    }
}
