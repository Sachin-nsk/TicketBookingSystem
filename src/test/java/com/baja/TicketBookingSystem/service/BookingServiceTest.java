package com.baja.TicketBookingSystem.service;

import com.baja.TicketBookingSystem.dto.BookingResponseDto;
import com.baja.TicketBookingSystem.entity.Booking;
import com.baja.TicketBookingSystem.entity.BookingStatus;
import com.baja.TicketBookingSystem.entity.Show;
import com.baja.TicketBookingSystem.entity.ShowSeat;
import com.baja.TicketBookingSystem.entity.Event;
import com.baja.TicketBookingSystem.entity.Seat;
import com.baja.TicketBookingSystem.entity.Auditorium; 
import com.baja.TicketBookingSystem.entity.User;
import com.baja.TicketBookingSystem.repository.BookingRepository;
import com.baja.TicketBookingSystem.repository.ShowRepository;
import com.baja.TicketBookingSystem.repository.ShowSeatRepository;
import com.baja.TicketBookingSystem.repository.UserRepository;
import com.baja.TicketBookingSystem.dto.BookingRequestDto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    // Create Booking test

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private ShowSeatRepository showSeatRepository;

    @Mock
    private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void createBooking_shouldReserveSeatsAndSendNotification() {
        String email = "user@test.com";
        BookingRequestDto request = new BookingRequestDto(1L, List.of(10L, 11L));

        User user = new User();
        user.setEmail(email);
        Show show = new Show();
        show.setStartTime(java.time.LocalDateTime.now());        
        Event event = new Event();
        event.setTitle("Test Movie");
        show.setEvent(event);
        Auditorium audi = new Auditorium();
        audi.setName("Audi 1");
        show.setAuditorium(audi);

        // Setup seats
        ShowSeat seat1 = new ShowSeat();
        seat1.setId(10L);
        seat1.setPrice(100);
        seat1.setStatus(com.baja.TicketBookingSystem.entity.ShowSeatStatus.AVAILABLE);
        Seat physicalSeat1 = new Seat();
        physicalSeat1.setSeatNumber(1);
        seat1.setSeat(physicalSeat1);

        ShowSeat seat2 = new ShowSeat();
        seat2.setId(11L);
        seat2.setPrice(100);
        seat2.setStatus(com.baja.TicketBookingSystem.entity.ShowSeatStatus.AVAILABLE);
        Seat physicalSeat2 = new Seat();
        physicalSeat2.setSeatNumber(2);
        seat2.setSeat(physicalSeat2);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(showSeatRepository.findAllByIdWithLock(request.getShowSeatIds())).thenReturn(List.of(seat1, seat2));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> {
            Booking b = i.getArgument(0);
            b.setId(999L);
            return b;
        });

        BookingResponseDto response = bookingService.createBooking(request, email);

        assertNotNull(response);
        assertEquals("CONFIRMED", response.getStatus());

        verify(kafkaTemplate).send(eq("booking-notifications"), any(com.baja.TicketBookingSystem.event.BookingCreatedEvent.class));

        assertEquals(com.baja.TicketBookingSystem.entity.ShowSeatStatus.BOOKED, seat1.getStatus());
        verify(showSeatRepository, times(2)).save(any(ShowSeat.class));


    }
}