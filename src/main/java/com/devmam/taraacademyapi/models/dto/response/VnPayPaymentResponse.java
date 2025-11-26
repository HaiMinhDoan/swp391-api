package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for VNPAY payment response
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VnPayPaymentResponse implements Serializable {
    private String paymentUrl;
    private Integer transactionId;
    private String message;
}

