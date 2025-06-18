package com.casadabencao.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Versiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String verse;

    @Column(columnDefinition = "TEXT")
    private String reference;

    // Construtor personalizado usado no seeder (sem ID)
    public Versiculo(String verse, String reference) {
        this.verse = verse;
        this.reference = reference;
    }
}
