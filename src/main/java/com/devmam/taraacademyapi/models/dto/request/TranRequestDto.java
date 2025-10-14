package com.devmam.taraacademyapi.models.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for Tran creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TranRequestDto {
    
    private Integer userId;
    
    private Integer courseId;
    
    private BigDecimal amount;
    
    private String paymentMethod;
    
    private String transactionId;
    
    private Instant transactionDate;
    
    private Integer status;
}
