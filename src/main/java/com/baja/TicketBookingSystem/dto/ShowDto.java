package com.baja.TicketBookingSystem.dto;

import lombok.Data;

@Data
public class ShowDto {
    private Long id;

    private String startTime;

    private String endTime;

    private Long auditoriumId;
    private String auditoriumName;
}