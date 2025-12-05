package com.devmam.taraacademyapi.models.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Message}
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class MessageDto implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 400)
    String content;
    @NotNull
    @Size(max = 200)
    String sendBy;
    Boolean isFromUser;
    Instant createdAt;
    Instant updatedAt;
    Integer status;
    Integer isDeleted;
}