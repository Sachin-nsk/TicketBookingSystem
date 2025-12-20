package com.baja.TicketBookingSystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class EventDto {
    private Long id;
    private String title;
    private String genre;
    private Integer durationMinutes;
    private List<ShowDto> shows;
}