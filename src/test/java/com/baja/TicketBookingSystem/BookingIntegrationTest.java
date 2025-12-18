package com.baja.TicketBookingSystem;

import com.baja.TicketBookingSystem.dto.BookingRequestDto;
import com.baja.TicketBookingSystem.entity.*;
import com.baja.TicketBookingSystem.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers 
@AutoConfigureMockMvc
class BookingIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private AuditoriumRepository auditoriumRepository;
    @Autowired
    private ShowRepository showRepository;

    @BeforeEach
    void setupData() {
        User user = new User();
        user.setName("Integration User");
        user.setEmail("test@test.com");
        user.setPassword("pass");
        userRepository.save(user);

        Venue venue = new Venue();
        venue.setName("Test Venue");
        venueRepository.save(venue);

        Auditorium audi = new Auditorium();
        audi.setName("Test Audi");
        audi.setVenue(venue);
        auditoriumRepository.save(audi);

        Event event = new Event();
        event.setTitle("Test Movie");
        eventRepository.save(event);

        Show show = new Show();
        show.setEvent(event);
        show.setAuditorium(audi);
        show.setStartTime(LocalDateTime.now().plusDays(1));
        show.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        showRepository.save(show);
    }

    @Test
    void createBooking_shouldPersistToRealDatabase() throws Exception {
        BookingRequestDto request = new BookingRequestDto(); 
        //request.setUserId(1L); 
        request.setShowId(1L);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.userName").value("Integration User"));
    }
}