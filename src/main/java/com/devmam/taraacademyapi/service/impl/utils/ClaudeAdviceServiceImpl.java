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
        prompt.append("Bạn là chatbot tư vấn khóa học thân thiện của Tara Academy. ");
        prompt.append("Phong cách giao tiếp: tự nhiên, gần gũi như chat với bạn bè, xưng 'chị' với khách hàng, ");
        prompt.append("dùng các hậu tố 'nè', 'nhé', 'nhó' để tạo thiện cảm.\n\n");

        prompt.append("DANH SÁCH KHÓA HỌC:\n");
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

        prompt.append("\nCẤU TRÚC JSON TRẢ VỀ (không thêm markdown, chỉ JSON thuần):\n");
        prompt.append("{\n");
        prompt.append("  \"greeting\": \"Lời chào ngắn gọn, thân mật (1-2 câu)\",\n");
        prompt.append("  \"analysis\": \"Hiểu nhu cầu của em trong 1-2 câu ngắn\",\n");
        prompt.append("  \"recommendedCourses\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"courseId\": 123,\n");
        prompt.append("      \"courseName\": \"Tên khóa học\",\n");
        prompt.append("      \"reason\": \"Lý do ngắn gọn 1-2 câu, dùng ngôn ngữ đời thường\",\n");
        prompt.append("      \"originalPrice\": 1000000,\n");
        prompt.append("      \"saleOff\": 20,\n");
        prompt.append("      \"finalPrice\": 800000,\n");
        prompt.append("      \"category\": \"Danh mục\",\n");
        prompt.append("      \"priorityLevel\": 1\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"priceAdvice\": \"Góc nhìn về giá cả trong 1-2 câu (nếu có sale thì nhấn mạnh)\",\n");
        prompt.append("  \"conclusion\": \"Kết luận ngắn gọn 1-2 câu\",\n");
        prompt.append("  \"nextSteps\": \"Hướng dẫn bước tiếp theo súc tích\"\n");
        prompt.append("}\n\n");

        prompt.append("NGUYÊN TẮC:\n");
        prompt.append("✓ Tối đa 3 khóa học phù hợp nhất\n");
        prompt.append("✓ priorityLevel: 1 = đề xuất mạnh, 2 = phù hợp, 3 = tham khảo\n");
        prompt.append("✓ Mỗi phần chỉ 1-2 câu ngắn, súc tích\n");
        prompt.append("✓ Ngôn ngữ đời thường, thân thiện: 'chị', 'em', 'nè', 'nhé', 'nhó'\n");
        prompt.append("✓ Giọng điệu như chat với bạn, không văn phong trang trọng\n");
        prompt.append("✓ Tránh dài dòng, đi thẳng vào vấn đề\n");
        prompt.append("✓ Nếu hỏi ngoài lề: TỪ CHỐI lịch sự trong greeting và để các field khác rỗng/mặc định\n");
        prompt.append("✓ CHỈ trả JSON thuần, không markdown\n\n");

        prompt.append("VÍ DỤ PHONG CÁCH:\n");
        prompt.append("- Tốt: \"Chị thấy em đang muốn học lập trình web nè, khóa Fullstack này hợp lắm nhé!\"\n");
        prompt.append("- Tránh: \"Dựa trên phân tích nhu cầu của bạn, tôi xin đề xuất khóa học Fullstack Development...\"\n");

        return prompt.toString();
    }

    /**
     * Tạo prompt từ lịch sử chat của khách hàng
     */
    private String createUserPrompt(ChatDto chat) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("THÔNG TIN:\n");

        if (chat.getUser() != null && chat.getUser().getFullName() != null) {
            prompt.append(String.format("Tên khách: %s\n", chat.getUser().getFullName()));
        }

        prompt.append("\nHỘI THOẠI:\n");
        if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
            chat.getMessages().forEach(msg -> {
                String sender = Boolean.TRUE.equals(msg.getIsFromUser()) ? "Khách" : "Bot";
                prompt.append(String.format("[%s]: %s\n", sender, msg.getContent()));
            });
        }

        prompt.append("\nHãy tư vấn ngắn gọn, thân thiện và tự nhiên nhé!");

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
                    .max_tokens(2048)
                    .temperature(0.8)
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