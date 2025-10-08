package com.devmam.taraacademyapi.models.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Slide}
 */
@Value
public class SlideResponseDto implements Serializable {
    Integer id;
    @Size(max = 255)
    String title;
    String description;
    @NotNull
    String imageUrl;
    String linkUrl;
    Integer orderIndex;
    UUID createdById;
    String createdByUsername;
    Instant createdAt;
    Instant updatedAt;
    Integer status;
}