package com.baja.TicketBookingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.baja.TicketBookingSystem.entity.Venue;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    
}