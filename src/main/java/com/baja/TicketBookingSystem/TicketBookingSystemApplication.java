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
}


