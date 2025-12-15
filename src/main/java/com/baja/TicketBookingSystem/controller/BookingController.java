package com.baja.TicketBookingSystem.controller;

import com.baja.TicketBookingSystem.dto.BookingRequestDto;
import com.baja.TicketBookingSystem.dto.BookingResponseDto; 
import com.baja.TicketBookingSystem.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingRequestDto bookingRequest) {
        BookingResponseDto newBooking = bookingService.createBooking(bookingRequest);
        return ResponseEntity.ok(newBooking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getBooking(@PathVariable Long id) {
        BookingResponseDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }
}