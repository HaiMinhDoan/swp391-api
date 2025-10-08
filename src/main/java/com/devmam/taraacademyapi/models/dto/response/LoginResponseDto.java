package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for Login response
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponseDto implements Serializable {
    String token;
    String tokenType;
    String email;
    String username;
    String fullName;
    String role;
    Set<String> roles;
    Long expiresIn;
}
