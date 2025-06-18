package com.casadabencao.backend.model;

// Entidade - Aviso.java
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Aviso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    private String arquivoUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCriacao;

    private LocalDate dataExpiracao;

    @Enumerated(EnumType.STRING)
    private TipoAviso tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    private Ministerio ministerio; // opcional, só se tipo == MINISTERIAL

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario autor;
}
