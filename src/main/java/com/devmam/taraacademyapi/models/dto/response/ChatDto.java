package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Chat}
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class ChatDto implements Serializable {
    Integer id;
    UUID userId;
    Instant createdAt;
    Instant updatedAt;
    Integer status;
    Boolean isAnonymous;
    Set<MessageDto> messages;
}