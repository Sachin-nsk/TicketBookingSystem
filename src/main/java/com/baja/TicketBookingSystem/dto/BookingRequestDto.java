package com.baja.TicketBookingSystem.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    private Long showId;
    private List<Long> showSeatIds;
}
