package com.casadabencao.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Ministerio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String leader;
    private String imageUrl;
    private String meetingDay;

    @ElementCollection
    @CollectionTable(name = "ministerio_atividades", joinColumns = @JoinColumn(name = "ministerio_id"))
    @Column(name = "atividade")
    private List<String> atividades;
}
