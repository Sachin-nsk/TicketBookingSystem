package com.baja.TicketBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity 
@Table(name = "shows")
@Data 
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    private LocalDateTime startTime;

    @JsonIgnore
    private LocalDateTime endTime;

    @JsonProperty("startTime")
    public String getFormattedStartTime() {
        if (startTime == null) return null;
        return startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @JsonProperty("endTime")
    public String getFormattedEndTime() {
        if (endTime == null) return null;
        return endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private Event event;

    @ManyToOne
    @JoinColumn(name = "auditorium_id", nullable = false)
    private Auditorium auditorium;
}
