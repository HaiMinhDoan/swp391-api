package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for User creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserRequestDto {
    
    @Size(max = 255, message = "Username must not exceed 255 characters")
    private String username;
    
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @Size(max = 255, message = "Password must not exceed 255 characters")
    private String password;
    
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;
    
    @Size(max = 255, message = "Phone must not exceed 255 characters")
    private String phone;
    
    @Size(max = 255, message = "Role must not exceed 255 characters")
    private String role;
    
    private Integer status;
}
