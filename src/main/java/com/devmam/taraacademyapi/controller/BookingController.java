package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BookingRequestDto;
import com.devmam.taraacademyapi.models.dto.response.BookingResponseDto;
import com.devmam.taraacademyapi.models.entities.Booking;
import com.devmam.taraacademyapi.models.entities.Ser;
import com.devmam.taraacademyapi.service.impl.entities.BookingService;
import com.devmam.taraacademyapi.service.impl.entities.SerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/bookings")
@PreAuthorize("permitAll()")
public class BookingController extends BaseController<Booking, Integer, BookingRequestDto, BookingResponseDto> {

    @Autowired
    private SerService serService;

    public BookingController(BookingService bookingService) {
        super(bookingService);
    }

    @Override
    protected BookingResponseDto toResponseDto(Booking booking) {
        return BookingResponseDto.toDTO(booking);
    }

    @Override
    protected Booking toEntity(BookingRequestDto requestDto) {
        // Get service entity
        Ser service = null;
        if (requestDto.getServiceId() != null) {
            service = serService.getOne(requestDto.getServiceId()).orElse(null);
        }

        Booking booking = new Booking();
        booking.setAccountId(requestDto.getAccountId());
        booking.setService(service);
        booking.setBookingDate(requestDto.getBookingDate());
        booking.setNote(requestDto.getNote());
        booking.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        booking.setIsDeleted(0);
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());

        return booking;
    }

    @Override
    protected Page<BookingResponseDto> convertPage(Page<Booking> bookingPage) {
        return BookingResponseDto.convertPage(bookingPage);
    }

    @Override
    protected String getEntityName() {
        return "Booking";
    }
}
