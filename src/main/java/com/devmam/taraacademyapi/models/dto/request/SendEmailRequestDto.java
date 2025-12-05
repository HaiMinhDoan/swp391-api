package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.UUID;

/**
 * DTO for sending email with HTML support
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SendEmailRequestDto {
    
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Recipient email should be valid")
    private String recipientEmail;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Is HTML flag is required")
    private Boolean isHtml;
    
    private Map<String, Object> parameters;
    
    private UUID createdById;
    
    private Integer applyId;
}

