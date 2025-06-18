package com.casadabencao.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Oracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID único da oração

    private String name; // Nome do usuário ou "Anônimo" (definido pelo frontend)

    @Column(columnDefinition = "TEXT")
    private String message; // Pedido de oração

    @CreationTimestamp
    private LocalDate date; // Data automática no momento do envio

    @Column(nullable = false)
    private Boolean responded = false; // True quando Deus respondeu, falso inicialmente

    private String category; // Categoria como Cura, Família, etc. (enviada pelo frontend)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario; // Relacionamento com o usuário logado (para controle e segurança)
}

