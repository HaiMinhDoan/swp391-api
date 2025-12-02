package com.devmam.taraacademyapi.models.dto.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminPieChartDataDto implements Serializable {
    private String name; // Category/Label name
    private Long value; // Count value
    private BigDecimal amount; // Amount value (for revenue)
    private String color; // Optional color for chart
    private Double percentage; // Percentage of total
}

