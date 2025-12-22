package com.devmam.taraacademyapi.models.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClaudeAdviceRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        String role; // "user" hoặc "assistant"
        String content;
    }

    String model;
    Integer max_tokens;
    List<Message> messages;
    Double temperature; // 0.0 - 1.0, cao hơn = sáng tạo hơn
}