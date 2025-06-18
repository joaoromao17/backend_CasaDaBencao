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
public class Testemunho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Nome do usuário ou "Anônimo" (controlado no frontend)

    @Column(columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    private LocalDate date;

    private String category;

    /**
     * Se for um testemunho vindo de uma oração respondida,
     * esse campo será true.
     * Se o testemunho foi direto (sem vínculo com oração), será null.
     */
    private Boolean responded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    /**
     * Se for originado de uma oração, esse campo aponta para ela.
     * Se for um testemunho direto, será null.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oracao_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Oracao oracaoOriginal;
}
