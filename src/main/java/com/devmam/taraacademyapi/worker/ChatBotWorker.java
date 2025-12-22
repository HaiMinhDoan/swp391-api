package com.devmam.taraacademyapi.worker;

import com.devmam.taraacademyapi.mapper.ChatMapper;
import com.devmam.taraacademyapi.models.dto.response.ChatDto;
import com.devmam.taraacademyapi.models.dto.response.CourseAdviceDto;
import com.devmam.taraacademyapi.models.dto.response.CourseResponseDto;
import com.devmam.taraacademyapi.models.dto.response.MessageDto;
import com.devmam.taraacademyapi.models.entities.Chat;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.Message;
import com.devmam.taraacademyapi.service.ClaudeAdviceService;
import com.devmam.taraacademyapi.service.impl.entities.ChatService;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Slf4j
@Getter
@Setter
public class ChatBotWorker {
    private Boolean isRunning = false;

    @Autowired
    private ChatService chatService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private ClaudeAdviceService claudeAdviceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 60000) // Chạy mỗi 1 phút
    public void runBotJob() {
        if (isRunning) {
            log.info("ChatBotWorker is already running, skipping this execution");
            return;
        }

        try {
            isRunning = true;
            log.info("Starting ChatBotWorker");

            // Lấy các chat chưa trả lời (status = 1)
            List<Chat> unReplyChats = chatService.findByStatusOrderByUpdatedAtDesc(1);

            if (unReplyChats.isEmpty()) {
                log.info("No unreplied chats found");
                return;
            }

            log.info("Found {} unreplied chats", unReplyChats.size());

            // Lấy danh sách khóa học đang hiển thị
            List<Course> courses = courseService.findAllShowing();
            List<CourseResponseDto> courseDtos = CourseResponseDto.toDtoList(courses);

            log.info("Found {} available courses", courseDtos.size());

            // Xử lý từng chat
            for (Chat chat : unReplyChats) {
                try {
                    processChat(chat, courseDtos);
                } catch (Exception e) {
                    log.error("Error processing chat ID: {}", chat.getId(), e);
                    // Tiếp tục với chat tiếp theo thay vì dừng toàn bộ worker
                }
            }

            log.info("ChatBotWorker completed successfully");

        } catch (Exception e) {
            log.error("Error in ChatBotWorker", e);
        } finally {
            isRunning = false;
        }
    }

    /**
     * Xử lý một chat cụ thể
     */
    private void processChat(Chat chat, List<CourseResponseDto> availableCourses) {
        log.info("Processing chat ID: {}", chat.getId());

        try {
            // Convert sang DTO
            ChatDto chatDto = chatMapper.toDto(chat);

            // Gọi Claude API để nhận tư vấn
            CourseAdviceDto advice = claudeAdviceService.getAdviceForChat(chatDto, availableCourses);

            // Tạo message trả lời
            String replyContent = formatAdviceToMessage(advice);

            // Lưu message vào database
            Message message = new Message();
            message.setChat(chat);
            message.setContent(replyContent);
            message.setSendBy("SYSTEM");
            message.setIsFromUser(false);
            message.setCreatedAt(Instant.now());
            message.setUpdatedAt(Instant.now());
            message.setStatus(1);
            message.setIsDeleted(0);

            messageService.create(message);

            // Cập nhật status của chat thành đã trả lời (status = 2)
            chat.setStatus(2);
            chat.setUpdatedAt(Instant.now());
            chatService.save(chat);

            log.info("Successfully processed chat ID: {} with advice", chat.getId());

        } catch (Exception e) {
            log.error("Failed to process chat ID: {}", chat.getId(), e);

            // Gửi message lỗi cho khách hàng
            try {

                Message errorMessage = new Message();
                errorMessage.setChat(chat);
                errorMessage.setContent("Xin lỗi, hiện tại hệ thống đang gặp sự cố. Vui lòng thử lại sau hoặc liên hệ trực tiếp với chúng tôi để được hỗ trợ tốt nhất.");
                errorMessage.setSendBy("SYSTEM");
                errorMessage.setIsFromUser(false);
                errorMessage.setCreatedAt(Instant.now());
                errorMessage.setUpdatedAt(Instant.now());
                errorMessage.setStatus(1);
                errorMessage.setIsDeleted(0);

                messageService.create(errorMessage);

                // Đánh dấu chat là đã xử lý (có lỗi)
                chat.setStatus(3); // 3 = error
                chat.setUpdatedAt(Instant.now());
                chatService.save(chat);

            } catch (Exception saveError) {
                log.error("Failed to save error message for chat ID: {}", chat.getId(), saveError);
            }
        }
    }

    /**
     * Format advice thành message cho khách hàng
     */
    private String formatAdviceToMessage(CourseAdviceDto advice) {
        StringBuilder message = new StringBuilder();

        // Lời chào
        if (advice.getGreeting() != null) {
            message.append(advice.getGreeting()).append("\n\n");
        }

        // Phân tích nhu cầu
        if (advice.getAnalysis() != null) {
            message.append("📊 Phân tích nhu cầu:\n");
            message.append(advice.getAnalysis()).append("\n\n");
        }

        // Danh sách khóa học đề xuất
        if (advice.getRecommendedCourses() != null && !advice.getRecommendedCourses().isEmpty()) {
            message.append("🎓 Các khóa học phù hợp cho bạn:\n\n");

            int index = 1;
            for (CourseAdviceDto.RecommendedCourse course : advice.getRecommendedCourses()) {
                String priority = "";
                if (course.getPriorityLevel() == 1) {
                    priority = "⭐ ĐỀ XUẤT MẠNH";
                } else if (course.getPriorityLevel() == 2) {
                    priority = "✨ PHÙ HỢP";
                } else {
                    priority = "💡 CÓ THỂ XEM XÉT";
                }

                message.append(String.format("%d. %s %s\n", index++, priority, course.getCourseName()));
                message.append(String.format("   📁 Danh mục: %s\n", course.getCategory()));

                if (course.getSaleOff() != null && course.getSaleOff() > 0) {
                    message.append(String.format("   💰 Giá: %,.0f VNĐ ~~%,.0f VNĐ~~ (Giảm %d%%)\n",
                            course.getFinalPrice(), course.getOriginalPrice(), course.getSaleOff()));
                } else {
                    message.append(String.format("   💰 Giá: %,.0f VNĐ\n", course.getOriginalPrice()));
                }

                message.append(String.format("   ✅ Lý do: %s\n\n", course.getReason()));
            }
        }

        // Tư vấn giá
        if (advice.getPriceAdvice() != null) {
            message.append("💎 Về giá cả:\n");
            message.append(advice.getPriceAdvice()).append("\n\n");
        }

        // Kết luận
        if (advice.getConclusion() != null) {
            message.append("🎯 Tổng kết:\n");
            message.append(advice.getConclusion()).append("\n\n");
        }

        // Các bước tiếp theo
        if (advice.getNextSteps() != null) {
            message.append("📝 Các bước tiếp theo:\n");
            message.append(advice.getNextSteps()).append("\n\n");
        }

        message.append("---\n");
        message.append("Nếu bạn cần thêm thông tin hoặc tư vấn chi tiết hơn, đừng ngần ngại liên hệ với chúng tôi! 💬");

        return message.toString();
    }
}