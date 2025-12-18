package com.baja.TicketBookingSystem.service;

import com.baja.TicketBookingSystem.entity.*;
import com.baja.TicketBookingSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final SeatRepository seatRepository;
    private final ShowSeatRepository showSeatRepository;

    @Transactional
    public void addSeatsToAuditorium(Auditorium auditorium, int rows, int seatsPerRow) {
        for(int i = 0; i < rows; i++) {
            char rowChar = (char) ('A' + i);
            for(int j = 1; j <= seatsPerRow; j++) {
                Seat seat = new Seat();
                seat.setAuditorium(auditorium);
                seat.setRowName(String.valueOf(rowChar));
                seat.setSeatNumber(j);
                seat.setType("REGULAR");
                seatRepository.save(seat);
            }
        }
    }

    @Transactional
    public void initializeShowSeats(Show show) {
        for(Seat seat: seatRepository.findAll()) {
            if(seat.getAuditorium().getId().equals(show.getAuditorium().getId())) {
                ShowSeat showSeat = new ShowSeat();
                showSeat.setShow(show);
                showSeat.setSeat(seat);
                showSeat.setStatus(ShowSeatStatus.AVAILABLE);
                showSeat.setPrice(100);
                showSeatRepository.save(showSeat);
            }
        }
    }
}
