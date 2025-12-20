package com.baja.TicketBookingSystem.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreatedEvent {
    private String userEmail;
    private String movieTitle;
    private String bookingId;
    private Double amount;
}
