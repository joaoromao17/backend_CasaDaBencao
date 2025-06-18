package com.casadabencao.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private String createdBy;

    private String pixKey;

    public enum Status {
        ATIVA,
        CONCLUIDA,
        CANCELADA
    }
}
