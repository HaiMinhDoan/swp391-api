package com.devmam.swp391api.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailSendRequestDTO {
    Integer id;
    @NotBlank
    @Email
    String recipientEmail;
    @NotBlank
    String subject;
    @NotBlank
    String content;
}