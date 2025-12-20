package com.baja.TicketBookingSystem.service;

import com.baja.TicketBookingSystem.dto.EventDto; 
import com.baja.TicketBookingSystem.dto.ShowDto; 
import com.baja.TicketBookingSystem.entity.Event;
import com.baja.TicketBookingSystem.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    @Transactional
    @Cacheable(value = "events")
    public List<EventDto> getAllEvents() {
        System.out.println("Fetching Events from Database (Not Cached)");
        List<Event> events =  eventRepository.findAll();

       return events.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private EventDto mapToDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setGenre(event.getGenre());
        dto.setDurationMinutes(event.getDurationMinutes());

        if(event.getShows() != null) {
            List<ShowDto> showDtos = event.getShows().stream().map(show -> {
                ShowDto sDto = new ShowDto();
                sDto.setId(show.getId());

                if (show.getStartTime() != null) {
                    sDto.setStartTime(show.getStartTime().toString());
                }
                if (show.getEndTime() != null) {
                    sDto.setEndTime(show.getEndTime().toString());
                }
                
                sDto.setAuditoriumId(show.getAuditorium().getId());
                sDto.setAuditoriumName(show.getAuditorium().getName());
                return sDto;
            }).collect(Collectors.toList());
            dto.setShows(showDtos);
        }

        return dto;
    }

    public List<Event> searchEvents(String genre) {
        return eventRepository.findByGenre(genre);
    }
}
