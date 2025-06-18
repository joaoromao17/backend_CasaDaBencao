package com.casadabencao.backend.dto;

import com.casadabencao.backend.model.TipoAviso;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NovoAvisoDto {

    @NotBlank
    private String titulo;

    private String mensagem;

    private String arquivoUrl;

    private TipoAviso tipo;
    private Long ministerioId; // opcional
    private LocalDate dataExpiracao; // opcional
}