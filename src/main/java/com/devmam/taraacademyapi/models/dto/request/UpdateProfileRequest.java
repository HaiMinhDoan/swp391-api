package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for updating user profile
 * Note: Cannot update createdAt, updatedAt (auto), status, isDeleted, password (use change-password API)
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpdateProfileRequest {
    
    @Size(max = 100, message = "Username must not exceed 100 characters")
    private String username;
    
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @Size(max = 50, message = "Customer code must not exceed 50 characters")
    private String customerCode;
    
    private BigDecimal accountBalance;
    
    @Size(max = 250, message = "Avatar URL must not exceed 250 characters")
    private String avt;
    
    @Size(max = 250, message = "Role must not exceed 250 characters")
    private String role;
}

