package com.baja.TicketBookingSystem.util;

import com.baja.TicketBookingSystem.entity.*;
import com.baja.TicketBookingSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final VenueRepository venueRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (venueRepository.count() > 0) return;

        System.out.println("--- SEEDING DATA START ---");

        // 1. Create Venue & Auditorium
        Venue venue = new Venue();
        venue.setName("PVR Cinemas");
        venue.setLocation("Mumbai");
        venueRepository.save(venue);

        Auditorium audi = new Auditorium();
        audi.setName("Audi 1");
        audi.setCapacity(50);
        audi.setVenue(venue);
        auditoriumRepository.save(audi);

        // 2. Create Physical Seats (Row A, Seats 1-5)
        for (int i = 1; i <= 5; i++) {
            Seat seat = new Seat();
            seat.setRowName("A");
            seat.setSeatNumber(i);
            seat.setType("PREMIUM");
            seat.setAuditorium(audi);
            seatRepository.save(seat);
        }

        // 3. Create Event & Show
        Event event = new Event();
        event.setTitle("Inception");
        event.setGenre("Sci-Fi");
        event.setDurationMinutes(150);
        eventRepository.save(event);

        Show show = new Show();
        show.setEvent(event);
        show.setAuditorium(audi);
        show.setStartTime(LocalDateTime.now().plusDays(1));
        show.setEndTime(LocalDateTime.now().plusDays(1).plusHours(3));
        showRepository.save(show);

        // 4. Create ShowSeats 
        for (Seat seat : seatRepository.findAll()) {
            ShowSeat showSeat = new ShowSeat();
            showSeat.setSeat(seat);
            showSeat.setShow(show);
            showSeat.setStatus(ShowSeatStatus.AVAILABLE);
            showSeat.setPrice(250);
            showSeatRepository.save(showSeat);
        }

        // 5. Create a Test User
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setPhoneNumber("9876543210");
        userRepository.save(user);

        System.out.println("--- SEEDING DATA COMPLETE ---");
    }
}