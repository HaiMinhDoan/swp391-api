package com.devmam.taraacademyapi.models.dto.request;

import com.devmam.taraacademyapi.models.dto.IBaseDTO;
import com.devmam.taraacademyapi.models.entities.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginDTO {

    @NotBlank(message = "UserAgent is required")
    String userAgent;

    @NotBlank(message = "Username or email is required")
    String usernameOrEmail;

    @NotBlank(message = "Password is required")
    String password;
}
