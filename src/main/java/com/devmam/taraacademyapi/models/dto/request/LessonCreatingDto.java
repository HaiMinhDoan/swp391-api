package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LessonCreatingDto {
    private Integer stageId;

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String content;

    private Integer orderIndex;

    private Integer status;
}
