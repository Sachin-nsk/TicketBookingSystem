package com.baja.TicketBookingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.baja.TicketBookingSystem.entity.Auditorium;

import java.util.List;

@Repository
public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {
    List<Auditorium> findByVenueId(Long venueId);
}