package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for Contact creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ContactRequestDto {
    
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;
    
    @Size(max = 255, message = "Phone must not exceed 255 characters")
    private String phone;
    
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @Size(max = 255, message = "Company must not exceed 255 characters")
    private String company;
    
    @Size(max = 255, message = "Personal role must not exceed 255 characters")
    private String personalRole;
    
    @Size(max = 255, message = "Subject must not exceed 255 characters")
    private String subject;
    
    private String message;
    
    private Integer servicesId;
    
    private Integer status;
}
