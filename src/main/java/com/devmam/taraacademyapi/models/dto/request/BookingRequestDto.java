package com.devmam.taraacademyapi.models.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

/**
 * DTO for Booking creation and update requests
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BookingRequestDto {
    
    private Integer accountId;
    
    private Integer serviceId;
    
    private Instant bookingDate;
    
    @Size(max = 255, message = "Note must not exceed 255 characters")
    private String note;
    
    private Integer status;
}
