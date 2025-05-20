package com.casadabencao.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contribuicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal targetValue;

    @NotNull
    private BigDecimal collectedValue = BigDecimal.ZERO;

    private boolean isGoalVisible;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String createdBy;

    private String shortDescription;

    private String pixKey;

    @Column(columnDefinition = "TEXT")
    private String fullDescription;

    public enum Status {
        ATIVA,
        CONCLUIDA,
        CANCELADA
    }
}
