package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * DTO for assigning role to user
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AssignRoleRequestDto {
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(ROLE_ADMIN|ROLE_STUDENT|ROLE_TEACHER|ADMIN|USER)$", 
             message = "Role must be one of: ROLE_ADMIN, ROLE_STUDENT, ROLE_TEACHER, ADMIN, USER")
    private String role;
}

