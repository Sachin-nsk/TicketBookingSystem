package com.baja.TicketBookingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity 
@Data 
@NoArgsConstructor 
public class Event {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String genre;
    private Integer durationMinutes;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Show> shows;
}
