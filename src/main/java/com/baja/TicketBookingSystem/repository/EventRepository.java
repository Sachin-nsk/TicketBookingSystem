package com.baja.TicketBookingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.baja.TicketBookingSystem.entity.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Event findByTitle(String title);

    List<Event> findByGenre(String genre);
}