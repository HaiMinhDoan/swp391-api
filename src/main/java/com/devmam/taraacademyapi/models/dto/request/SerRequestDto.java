package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for Ser (Services) creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SerRequestDto {
    
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    private String description;
    
    private String detail;
    
    private BigDecimal price;
    
    @Size(max = 555, message = "Thumbnail must not exceed 555 characters")
    private String thumnail;
    
    private Integer status;
}
