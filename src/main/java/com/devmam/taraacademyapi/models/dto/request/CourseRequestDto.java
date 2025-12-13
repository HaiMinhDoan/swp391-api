package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for Course creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CourseRequestDto {
    
    private Integer categoryId;
    
    private String categoryName;
    
    private String thumnail;
    
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Size(max = 255, message = "Summary must not exceed 255 characters")
    private String summary;
    
    private String description;
    
    @Size(max = 255, message = "Language must not exceed 255 characters")
    private String lang;
    
    private BigDecimal price;
    
    private Integer saleOff;
    
    private Integer status;
    
    private String createdByUsername;
    
    @Size(max = 255, message = "Reject reason must not exceed 255 characters")
    private String rejectReason;
}
