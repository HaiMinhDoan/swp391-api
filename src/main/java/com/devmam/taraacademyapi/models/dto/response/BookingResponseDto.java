package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Booking;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Booking}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BookingResponseDto implements Serializable {
    private final Integer id;
    private final Integer accountId;
    private final Integer serviceId;
    private final String serviceName;
    private final Instant bookingDate;
    private final String note;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static BookingResponseDto toDTO(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .accountId(booking.getAccountId())
                .serviceId(booking.getService() != null ? booking.getService().getId() : null)
                .serviceName(booking.getService() != null ? booking.getService().getName() : null)
                .bookingDate(booking.getBookingDate())
                .note(booking.getNote())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .status(booking.getStatus())
                .isDeleted(booking.getIsDeleted())
                .build();
    }

    public static Page<BookingResponseDto> convertPage(Page<Booking> bookingPage) {
        List<BookingResponseDto> bookingResponseDTOs = bookingPage.getContent()
                .stream()
                .map(BookingResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                bookingResponseDTOs,
                bookingPage.getPageable(),
                bookingPage.getTotalElements()
        );
    }
}
