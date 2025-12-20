package com.baja.TicketBookingSystem.service;

import com.baja.TicketBookingSystem.event.BookingCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @KafkaListener(topics = "booking-notifications", groupId = "notification-group")
    public void handleBookingNotification(BookingCreatedEvent event) {
        System.out.println("----------------------");
        System.out.println("RECEIVED NOTIFICATION EVENT");
        System.out.println("Processing email for: " + event.getUserEmail());
        System.out.println("Content: Your booking for '" + event.getMovieTitle() + "' is confirmed");
        System.out.println("Booking ID: " + event.getBookingId());
        System.out.println("----------------------");
    }
}
