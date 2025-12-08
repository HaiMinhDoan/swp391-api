package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.mapper.ChatMapper;
import com.devmam.taraacademyapi.mapper.MessageMapper;
import com.devmam.taraacademyapi.models.dto.response.ChatDto;
import com.devmam.taraacademyapi.models.dto.response.MessageDto;
import com.devmam.taraacademyapi.models.entities.Chat;
import com.devmam.taraacademyapi.models.entities.Message;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.repository.ChatRepository;
import com.devmam.taraacademyapi.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserService userService;

    /**
     * Tạo anonymous chat (user chưa đăng nhập)
     */
    @Transactional
    public ChatDto createAnonymousChat() {
        Chat chat = new Chat();
        chat.setUserId(null);
        chat.setIsAnonymous(true);
        chat.setCreatedAt(Instant.now());
        chat.setUpdatedAt(Instant.now());
        chat.setStatus(1);

        chat = chatRepository.save(chat);
        return chatMapper.toDto(chat);
    }

    /**
     * Tạo chat cho user đã đăng nhập
     */
    @Transactional
    public ChatDto createChatForUser(UUID userId) {
        // Verify user exists
        Optional<User> userOpt = userService.getOne(userId);
        if (userOpt.isEmpty()) {
            throw new CommonException("User not found with ID: " + userId);
        }

        Chat chat = new Chat();
        chat.setUserId(userId);
        chat.setIsAnonymous(false);
        chat.setCreatedAt(Instant.now());
        chat.setUpdatedAt(Instant.now());
        chat.setStatus(1);

        chat = chatRepository.save(chat);
        return chatMapper.toDto(chat);
    }

    /**
     * Link anonymous chat với user (khi user đăng nhập sau)
     */
    @Transactional
    public ChatDto linkAnonymousChatToUser(Integer chatId, UUID userId) {
        // Verify user exists
        Optional<User> userOpt = userService.getOne(userId);
        if (userOpt.isEmpty()) {
            throw new CommonException("User not found with ID: " + userId);
        }

        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            throw new CommonException("Chat not found with ID: " + chatId);
        }

        Chat chat = chatOpt.get();

        // Chỉ cho phép link nếu chat là anonymous
        if (!Boolean.TRUE.equals(chat.getIsAnonymous())) {
            throw new CommonException("Chat is not anonymous");
        }

        // Link chat với user
        chat.setUserId(userId);
        chat.setIsAnonymous(false);
        chat.setUpdatedAt(Instant.now());

        chat = chatRepository.save(chat);
        return chatMapper.toDto(chat);
    }

    /**
     * Lấy chat theo ID (anonymous)
     */
    public ChatDto getChatByIdAnonymous(Integer chatId) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            throw new CommonException("Chat not found with ID: " + chatId);
        }

        Chat chat = chatOpt.get();
        if (chat.getStatus() == 0) {
            throw new CommonException("Chat has been deleted");
        }

        return chatMapper.toDto(chat);
    }

    /**
     * Lấy chat theo ID cho user đã đăng nhập
     */
    public ChatDto getChatByIdForUser(Integer chatId, UUID userId) {
        // Verify user exists
        Optional<User> userOpt = userService.getOne(userId);
        if (userOpt.isEmpty()) {
            throw new CommonException("User not found with ID: " + userId);
        }

        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            throw new CommonException("Chat not found with ID: " + chatId);
        }

        Chat chat = chatOpt.get();

        // Verify ownership nếu chat không phải anonymous
        if (chat.getUserId() != null && !chat.getUserId().equals(userId)) {
            throw new CommonException("You don't have permission to access this chat");
        }

        if (chat.getStatus() == 0) {
            throw new CommonException("Chat has been deleted");
        }

        return chatMapper.toDto(chat);
    }

    /**
     * Lấy tất cả chats của user
     */
    public List<ChatDto> getUserChats(UUID userId) {
        // Verify user exists
        Optional<User> userOpt = userService.getOne(userId);
        if (userOpt.isEmpty()) {
            throw new CommonException("User not found with ID: " + userId);
        }

        List<Chat> chats = chatRepository.findByUserIdAndStatusOrderByUpdatedAtDesc(userId, 1);
        return chats.stream()
                .map(chatMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả chats (cho admin/support)
     */
    public List<ChatDto> getAllChats() {
        List<Chat> chats = chatRepository.findByStatusOrderByUpdatedAtDesc(1);
        return chats.stream()
                .map(chatMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả anonymous chats (cho admin/support)
     */
    public List<ChatDto> getAllAnonymousChats() {
        List<Chat> chats = chatRepository.findByIsAnonymousTrueAndStatusOrderByUpdatedAtDesc(1);
        return chats.stream()
                .map(chatMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Xóa chat (soft delete) - chỉ cho user đã đăng nhập
     */
    @Transactional
    public void deleteChat(Integer chatId, UUID userId) {
        // Verify user exists
        Optional<User> userOpt = userService.getOne(userId);
        if (userOpt.isEmpty()) {
            throw new CommonException("User not found with ID: " + userId);
        }

        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            throw new CommonException("Chat not found with ID: " + chatId);
        }

        Chat chat = chatOpt.get();

        // Verify ownership
        if (chat.getUserId() != null && !chat.getUserId().equals(userId)) {
            throw new CommonException("You don't have permission to delete this chat");
        }

        chat.setStatus(0); // Soft delete
        chat.setUpdatedAt(Instant.now());
        chatRepository.save(chat);
    }

    /**
     * Lưu và broadcast message
     */
    @Transactional
    public MessageDto saveAndBroadcast(Integer chatId, MessageDto dto) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);

        if (chatOpt.isEmpty()) {
            throw new CommonException("Chat not found with ID: " + chatId);
        }

        Chat chat = chatOpt.get();

        if (chat.getStatus() == 0) {
            throw new CommonException("Cannot send message to deleted chat");
        }

        Message message = new Message();
        message.setChat(chat);
        message.setContent(dto.getContent());
        message.setSendBy(dto.getSendBy());
        message.setIsFromUser(dto.getIsFromUser());
        message.setCreatedAt(Instant.now());
        message.setUpdatedAt(Instant.now());
        message.setStatus(1);
        message.setIsDeleted(0);

        message = messageRepository.save(message);

        // Update chat's updatedAt
        chat.setUpdatedAt(Instant.now());
        chatRepository.save(chat);

        return messageMapper.toDto(message);
    }

    /**
     * Lấy lịch sử chat (anonymous)
     */
    public List<MessageDto> getChatHistoryAnonymous(Integer chatId) {
        // Verify chat exists
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            throw new CommonException("Chat not found with ID: " + chatId);
        }

        return messageMapper.toDtoList(
                messageRepository.findByChatIdOrderByCreatedAtAsc(chatId)
        );
    }

    /**
     * Lấy lịch sử chat cho user đã đăng nhập
     */
    public List<MessageDto> getChatHistoryForUser(Integer chatId, UUID userId) {
        // Verify user exists
        Optional<User> userOpt = userService.getOne(userId);
        if (userOpt.isEmpty()) {
            throw new CommonException("User not found with ID: " + userId);
        }

        // Verify chat exists
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            throw new CommonException("Chat not found with ID: " + chatId);
        }

        Chat chat = chatOpt.get();

        // Verify ownership nếu chat không phải anonymous
        if (chat.getUserId() != null && !chat.getUserId().equals(userId)) {
            throw new CommonException("You don't have permission to access this chat");
        }

        return messageMapper.toDtoList(
                messageRepository.findByChatIdOrderByCreatedAtAsc(chatId)
        );
    }

    /**
     * Thông báo user join chat
     */
    public void notifyUserJoined(Integer chatId, User user) {
        String username = user != null ? user.getUsername() : "Guest";
        String role = user != null ? user.getRole() : "GUEST";

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId + "/joined",
                new JoinNotification(username, role)
        );
    }

    /**
     * Inner class cho join notification
     */
    public static class JoinNotification {
        private String username;
        private String role;

        public JoinNotification(String username, String role) {
            this.username = username;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }
    }
}