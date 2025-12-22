package com.devmam.taraacademyapi.models.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClaudeAdviceResponse {

    String id;
    String type;
    String role;
    List<Content> content;
    String model;

    @JsonProperty("stop_reason")
    String stopReason;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        String type;
        String text;
    }

    // Lấy text từ response
    public String getResponseText() {
        if (content != null && !content.isEmpty()) {
            return content.get(0).getText();
        }
        return null;
    }
}