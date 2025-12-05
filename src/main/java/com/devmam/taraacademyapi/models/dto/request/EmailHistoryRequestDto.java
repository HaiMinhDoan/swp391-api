package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

/**
 * DTO for EmailHistory creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EmailHistoryRequestDto {
    
    @NotNull(message = "Recipient email is required")
    @Email(message = "Recipient email should be valid")
    @Size(max = 255, message = "Recipient email must not exceed 255 characters")
    private String recipientEmail;
    
    @NotNull(message = "Subject is required")
    private String subject;
    
    @NotNull(message = "Content is required")
    private String content;
    
    @NotNull(message = "Status is required")
    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status;
    
    private String errorMessage;
    
    private UUID createdById;
    
    private Integer applyId;
}

