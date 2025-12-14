package com.baja.TicketBookingSystem.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity 
@Data
public class Venue {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String location;

}
