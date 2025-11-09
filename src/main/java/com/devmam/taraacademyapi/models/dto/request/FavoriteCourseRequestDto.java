package com.devmam.taraacademyapi.models.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavoriteCourseRequestDto {
    UUID userId;
    Integer courseId;
}
