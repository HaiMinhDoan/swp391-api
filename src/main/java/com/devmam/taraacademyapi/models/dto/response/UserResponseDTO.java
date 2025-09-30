package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.User}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO implements Serializable {
    UUID id;
    String username;
    String email;
    String fullName;
    String phone;
    String customerCode;
    BigDecimal accountBalance;
    String avt;
    String role;
    Instant createdAt;
    Instant updatedAt;
    Integer status;
    Integer isDeleted;

    public static UserResponseDTO toDTO(User m) throws UnsupportedOperationException {
        return UserResponseDTO.builder()
                .id(m.getId())
                .username(m.getUsername())
                .email(m.getEmail())
                .fullName(m.getFullName())
                .phone(m.getPhone())
                .customerCode(m.getCustomerCode())
                .accountBalance(m.getAccountBalance())
                .avt(m.getAvt())
                .role(m.getRole())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .status(m.getStatus())
                .isDeleted(m.getIsDeleted())
                .build();
    }

    public static User toModel() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public static Page<UserResponseDTO> convertPage(Page<User> userPage) {
        List<UserResponseDTO> userResponseDTOs = userPage.getContent()
                .stream()
                .map(UserResponseDTO::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                userResponseDTOs,
                userPage.getPageable(),
                userPage.getTotalElements()
        );
    }
}