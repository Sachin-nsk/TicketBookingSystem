package com.baja.TicketBookingSystem.controller;

import com.baja.TicketBookingSystem.dto.EventDto;
import com.baja.TicketBookingSystem.entity.Event;
import com.baja.TicketBookingSystem.service.EventService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam String genre) {
        return ResponseEntity.ok(eventService.searchEvents(genre));
    }
}
