package com.baja.TicketBookingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class Auditorium {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int capacity;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;
}
