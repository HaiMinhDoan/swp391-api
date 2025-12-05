package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

/**
 * DTO for Application creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ApplicationRequestDto {
    
    @NotNull(message = "Career ID is required")
    private Integer careerId;
    
    private String cvUrl;
    
    private Integer status;
    
    private Instant interviewDate;
    
    private String note;
    
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;
    
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @Size(max = 255, message = "Phone must not exceed 255 characters")
    private String phone;
    
    private Short gender;
    
    private Instant interviewDatetime;
    
    @Size(max = 255, message = "Interview type must not exceed 255 characters")
    private String interviewType;
    
    @Size(max = 255, message = "Meeting link must not exceed 255 characters")
    private String meetingLink;
    
    private String finalNote;
}

