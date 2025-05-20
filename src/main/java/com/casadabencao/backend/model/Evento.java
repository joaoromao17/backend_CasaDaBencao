package com.casadabencao.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;
    @Column(name = "date")
    private LocalDateTime date;
    private String time;
    private String imageUrl;
    private String category; // novo campo
}
