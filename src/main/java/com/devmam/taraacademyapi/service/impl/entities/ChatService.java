package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.mapper.MessageMapper;
import com.devmam.taraacademyapi.models.dto.response.MessageDto;
import com.devmam.taraacademyapi.models.entities.Chat;
import com.devmam.taraacademyapi.models.entities.Message;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.repository.ChatRepository;
import com.devmam.taraacademyapi.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public MessageDto saveAndBroadcast(Integer chatId, MessageDto dto) {
        Optional<Chat> chat = chatRepository.findById(chatId);

        if (chat.isEmpty()) {
            throw new CommonException("Chat not found with ID: " + chatId);
        }


        Message message = new Message();
        message.setContent(dto.getContent());
        message.setSendBy(dto.getSendBy());
        message.setIsFromUser(dto.getIsFromUser());
        message.setCreatedAt(Instant.now());

        message = messageRepository.save(message);

        // Convert to DTO and return
        return messageMapper.toDto(message);
    }

    public List<MessageDto> getChatHistory(Integer chatId) {
        return messageMapper.toDtoList(messageRepository.findByChatIdOrderByCreatedAtAsc(chatId));
    }

    public void notifyUserJoined(Long chatId, User principal) {
        String userId = principal != null ? principal.getUsername() : "Guest";
        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId + "/joined",
                userId
        );
    }
}