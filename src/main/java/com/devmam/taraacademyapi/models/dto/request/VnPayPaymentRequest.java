package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for VNPAY payment creation request
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VnPayPaymentRequest {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Course ID is required")
    private Integer courseId;
    
    private String orderInfo;
    
    private String orderType;
    
    private String bankCode;
    
    private String language;
}

