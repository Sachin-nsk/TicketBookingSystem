package com.baja.TicketBookingSystem.controller;

import com.baja.TicketBookingSystem.entity.ShowSeat;
import com.baja.TicketBookingSystem.repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {
    private final ShowSeatRepository showSeatRepository;

    @GetMapping("/{showId}/seats")
    public ResponseEntity<List<Map<String, Object>>> getShowSeats(@PathVariable Long showId) {
        List<ShowSeat> seats = showSeatRepository.findByShowId(showId);

        List<Map<String, Object>> response = seats.stream().map(seat -> Map.<String, Object>of(
            "seatId", seat.getId(),
            "row", seat.getSeat().getRowName(),
            "number", seat.getSeat().getSeatNumber(),
            "price", seat.getPrice(),
            "status", seat.getStatus()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
