package com.baja.TicketBookingSystem.service;

import com.baja.TicketBookingSystem.dto.BookingRequestDto;
import com.baja.TicketBookingSystem.dto.BookingResponseDto; 
import com.baja.TicketBookingSystem.entity.*;
import com.baja.TicketBookingSystem.exception.ResourceNotFoundException;
import com.baja.TicketBookingSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import com.baja.TicketBookingSystem.event.BookingCreatedEvent;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        List<ShowSeat> selectedSeats = showSeatRepository.findAllByIdWithLock(request.getShowSeatIds());

        if(selectedSeats.isEmpty() || selectedSeats.size() != request.getShowSeatIds().size()) {
            throw new ResourceNotFoundException("One or more seats not found");
        }

        for(ShowSeat seat: selectedSeats) {
            if(seat.getStatus() != ShowSeatStatus.AVAILABLE) {
                throw new IllegalStateException("Seat " + seat.getSeat().getSeatNumber() + " is already booked");
            }
        }


        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);
        
        Booking savedBooking = bookingRepository.save(booking);

        Double totalPrice = selectedSeats.stream().mapToDouble(ShowSeat::getPrice).sum();

        BookingCreatedEvent event = new BookingCreatedEvent(
            user.getEmail(),
            show.getEvent().getTitle(),
            savedBooking.getId().toString(),
            totalPrice
        );

        kafkaTemplate.send("booking-notifications", event);
        System.out.println("--- Kafka Message Sent ---");

        for(ShowSeat seat: selectedSeats) {
            seat.setStatus(ShowSeatStatus.BOOKED);
            seat.setBooking(savedBooking);
            showSeatRepository.save(seat);
        }

        return mapToDto(savedBooking);
    }

    public BookingResponseDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return mapToDto(booking);
    }

    private BookingResponseDto mapToDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setBookingId(booking.getId());
        dto.setBookingTime(booking.getBookingTime());
        dto.setStatus(booking.getStatus().toString());
        dto.setUserName(booking.getUser().getName()); 
        dto.setEventTitle(booking.getShow().getEvent().getTitle());
        dto.setShowTime(booking.getShow().getStartTime().toString());
        dto.setAuditoriumName(booking.getShow().getAuditorium().getName());
        return dto;
    }

    @Transactional
    public BookingResponseDto cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);
        
        return mapToDto(savedBooking);
    }
}