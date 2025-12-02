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
public class AdminLineChartDataDto implements Serializable {
    private String label; // Date/Time label (e.g., "2024-01", "2024-02")
    private Long value; // Count value
    private BigDecimal amount; // Amount value (for revenue)
    private Instant date; // Actual date for sorting
}

