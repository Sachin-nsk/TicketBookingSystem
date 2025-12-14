package com.baja.TicketBookingSystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.baja.TicketBookingSystem.entity.*;
import com.baja.TicketBookingSystem.repository.*;

import java.time.LocalDateTime;


@SpringBootApplication
public class TicketBookingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketBookingSystemApplication.class, args);
	}


	@Bean
	public CommandLineRunner dataLoader(
			UserRepository userRepository,
			EventRepository eventRepository,
			VenueRepository venueRepository,
			AuditoriumRepository auditoriumRepository,
			ShowRepository showRepository,
			BookingRepository bookingRepository) {

		return args -> {
			// Create a User
			User user = new User();
			user.setName("Sachin");
			user.setEmail("abc@gmail.com");
			user.setPassword("123");
			userRepository.save(user);
			System.out.println("User Created: " + user.getId());

			// Create an Event
			Event event = new Event();
			event.setTitle("Interstellar");
			event.setGenre("Sci-Fi");
			event.setDurationMinutes(169);
			eventRepository.save(event);
			System.out.println("Event Created: " + event.getId());

			// Create Venue & Auditorium
			Venue venue = new Venue();
			venue.setName("PVR Cinemas");
			venue.setLocation("Mumbai");
			venueRepository.save(venue); 

			Auditorium audi = new Auditorium();
			audi.setName("Screen 1 - IMAX");
			audi.setCapacity(200);
			audi.setVenue(venue); 
			auditoriumRepository.save(audi);
			System.out.println("Venue & Auditorium Created");

			// Create a Show
			Show show = new Show();
			show.setEvent(event);
			show.setAuditorium(audi);
			show.setStartTime(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0));
			show.setEndTime(LocalDateTime.now().plusDays(1).withHour(21).withMinute(0));
			showRepository.save(show);
			System.out.println("Show Created: " + show.getId());

			// Create a Booking
			Booking booking = new Booking();
			booking.setUser(user);
			booking.setShow(show);
			booking.setBookingTime(LocalDateTime.now());
			booking.setStatus(BookingStatus.CONFIRMED);
			bookingRepository.save(booking);
			System.out.println("Booking Created: " + booking.getId());
			
		
	};

}
}


