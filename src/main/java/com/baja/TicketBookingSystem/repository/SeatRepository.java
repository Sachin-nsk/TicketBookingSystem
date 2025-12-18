package com.baja.TicketBookingSystem.repository;

import com.baja.TicketBookingSystem.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}