package com.baja.TicketBookingSystem;

import com.baja.TicketBookingSystem.dto.BookingRequestDto;
import com.baja.TicketBookingSystem.entity.*;
import com.baja.TicketBookingSystem.repository.*;
import com.baja.TicketBookingSystem.util.JwtUtil; 
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName; 

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class BookingIntegrationTest {

    static {
        System.setProperty("testcontainers.ryuk.disabled", "true");
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
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
    @Autowired
    private SeatRepository seatRepository;       
    @Autowired
    private ShowSeatRepository showSeatRepository; 
    @Autowired
    private JwtUtil jwtUtil;                   
    @Autowired
    private PasswordEncoder passwordEncoder;   

    private String testToken;
    private Long testShowId;
    private Long testShowSeatId;

    @BeforeEach
    void setupData() {
        // Clear DB 
        showSeatRepository.deleteAllInBatch();
        bookingRepository.deleteAllInBatch(); 
        showRepository.deleteAllInBatch();
        seatRepository.deleteAllInBatch();
        auditoriumRepository.deleteAllInBatch();
        venueRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        // 1. Create User & Generate Token
        User user = new User();
        user.setName("Integration User");
        user.setEmail("test@test.com");
        user.setPassword(passwordEncoder.encode("pass")); 
        userRepository.save(user);
        
        // Generate Token
        testToken = jwtUtil.generateToken("test@test.com");

        // 2. Create Venue & Audi
        Venue venue = new Venue();
        venue.setName("Test Venue");
        venueRepository.save(venue);

        Auditorium audi = new Auditorium();
        audi.setName("Test Audi");
        audi.setVenue(venue);
        audi.setCapacity(50);
        auditoriumRepository.save(audi);

        // 3. Create Event & Show
        Event event = new Event();
        event.setTitle("Test Movie");
        eventRepository.save(event);

        Show show = new Show();
        show.setEvent(event);
        show.setAuditorium(audi);
        show.setStartTime(LocalDateTime.now().plusDays(1));
        show.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        showRepository.save(show);
        testShowId = show.getId();

        // 4. Create Physical Seat
        Seat seat = new Seat();
        seat.setAuditorium(audi);
        seat.setRowName("A");
        seat.setSeatNumber(1);
        seat.setType("REGULAR");
        seatRepository.save(seat);

        // 5. Create ShowSeat
        ShowSeat showSeat = new ShowSeat();
        showSeat.setShow(show);
        showSeat.setSeat(seat);
        showSeat.setStatus(ShowSeatStatus.AVAILABLE);
        showSeat.setPrice(100.0);
        showSeatRepository.save(showSeat);
        testShowSeatId = showSeat.getId();
    }

    @Autowired
    private BookingRepository bookingRepository; 

    @Test
    void createBooking_shouldPersistToRealDatabase() throws Exception {
        // 1. Create Request DTO 
        BookingRequestDto request = new BookingRequestDto();
        request.setShowId(testShowId);
        request.setShowSeatIds(List.of(testShowSeatId));

        // 2. Perform Request with Authorization Header
        mockMvc.perform(post("/api/bookings")
                .header("Authorization", "Bearer " + testToken) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.userName").value("Integration User"));
    }

    @Test
    void testConcurrency_showPreventDoubleBooking() throws Exception {
        String user2Email = "user2@test.com";
        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail(user2Email);
        user2.setPassword("password");
        userRepository.save(user2);
        String token2 = jwtUtil.generateToken(user2Email);

        BookingRequestDto request = new BookingRequestDto();
        request.setShowId(testShowId);
        request.setShowSeatIds(List.of(testShowSeatId));

        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(2);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(2);

        Runnable task1 = () -> {
            try {
                mockMvc.perform(post("/api/bookings")
                    .header("Authorization", "Bearer " + testToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(request)));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        };

        Runnable task2 = () -> {
            try {
                mockMvc.perform(post("/api/bookings")
                    .header("Authorization", "Bearer " + token2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(request)));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        };

        executor.submit(task1);
        executor.submit(task2);
        latch.await();

        long bookingCount = bookingRepository.count();
        List<com.baja.TicketBookingSystem.entity.ShowSeat> seats = showSeatRepository.findAll();
        long bookedSeats = seats.stream().filter(s -> s.getStatus() == com.baja.TicketBookingSystem.entity.ShowSeatStatus.BOOKED).count();

        assertEquals(1, bookedSeats, "Only one booking should succeed");


    }
}