package com.casadabencao.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column(columnDefinition = "TEXT")
    private String description;

    // Relação com os líderes (pode ser mais de um)
    @ManyToMany
    @JoinTable(
            name = "ministerio_leaders",
            joinColumns = @JoinColumn(name = "ministerio_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @JsonIgnoreProperties({"ministries", "roles", "password"})
    private List<Usuario> leaders;

    @ManyToMany
    @JoinTable(
            name = "ministerio_vice_leaders",
            joinColumns = @JoinColumn(name = "ministerio_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @JsonIgnoreProperties({"ministries", "roles", "password"})
    private List<Usuario> viceLeaders;

    @Column(length = 512)
    private String imageUrl;

    private String meetingDay;

    @ElementCollection
    @CollectionTable(name = "ministerio_atividades", joinColumns = @JoinColumn(name = "ministerio_id"))
    @Column(name = "atividade")
    private List<String> atividades;

    // Mural para avisos
    @Column(columnDefinition = "TEXT")
    private String wall;
}