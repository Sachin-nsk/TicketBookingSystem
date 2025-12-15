package com.baja.TicketBookingSystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponseDto {
    private Long bookingId;
    private LocalDateTime bookingTime;
    private String status;
    private String userName;
    private String eventTitle;
    private String showTime;
    private String auditoriumName;
}
