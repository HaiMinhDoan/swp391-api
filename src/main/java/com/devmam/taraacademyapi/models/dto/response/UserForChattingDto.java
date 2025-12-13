package com.devmam.taraacademyapi.models.dto.response;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.User}
 */
@AllArgsConstructor
@Getter
public class UserForChattingDto implements Serializable {
    private final UUID id;
    @Size(max = 100)
    private final String username;
    @Size(max = 255)
    private final String email;
    @Size(max = 255)
    private final String fullName;
    @Size(max = 20)
    private final String phone;
    @Size(max = 250)
    private final String avt;
    @Size(max = 50)
    private final String customerCode;
    private final BigDecimal accountBalance;
    @Size(max = 250)
    private final String role;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;
    @Size(max = 100)
    private final String password;
}