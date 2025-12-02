package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherRevenueDto implements Serializable {
    private String period; // "2024-01", "2024-02", etc.
    private BigDecimal revenue;
    private Long transactionCount;
    private Instant startDate;
    private Instant endDate;
}

