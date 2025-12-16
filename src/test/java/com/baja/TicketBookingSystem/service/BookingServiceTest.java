package com.baja.TicketBookingSystem.service;

import com.baja.TicketBookingSystem.dto.BookingResponseDto;
import com.baja.TicketBookingSystem.entity.Booking;
import com.baja.TicketBookingSystem.entity.BookingStatus;
import com.baja.TicketBookingSystem.entity.Show; 
import com.baja.TicketBookingSystem.entity.Event; 
import com.baja.TicketBookingSystem.entity.Auditorium; 
import com.baja.TicketBookingSystem.entity.User;
import com.baja.TicketBookingSystem.repository.BookingRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock 
    private BookingRepository bookingRepository;

    @InjectMocks 
    private BookingService bookingService;

    @Test 
    void cancelBooking_shouldUpdateStatusToCancelled() {
        Long bookingId = 1L;

        Auditorium audi = new Auditorium();
        audi.setName("Test Audi");

        Event event = new Event();
        event.setTitle("Test Event");

        Show show = new Show();
        show.setEvent(event);
        show.setAuditorium(audi);
        show.setStartTime(LocalDateTime.now());

        User user = new User();
        user.setName("Sachin");

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setShow(show);
        booking.setUser(user);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        BookingResponseDto result = bookingService.cancelBooking(bookingId);

        assertEquals("CANCELLED", result.getStatus()); 

        verify(bookingRepository).save(booking);
    }
}