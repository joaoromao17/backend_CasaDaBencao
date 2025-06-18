package com.casadabencao.backend.dto;

// DTO - AvisoDto.java
import com.casadabencao.backend.model.Aviso;
import com.casadabencao.backend.model.TipoAviso;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AvisoDto {
    private Long id;
    private String titulo;
    private String mensagem;
    private String arquivoUrl;
    private LocalDateTime dataCriacao;
    private LocalDate dataExpiracao;
    private TipoAviso tipo;
    private String nomeMinisterio;
    private String nomeAutor;

    // construtor com base no model
    public AvisoDto(Aviso aviso) {
        this.id = aviso.getId();
        this.titulo = aviso.getTitulo();
        this.mensagem = aviso.getMensagem();
        this.arquivoUrl = aviso.getArquivoUrl();
        this.dataCriacao = aviso.getDataCriacao();
        this.dataExpiracao = aviso.getDataExpiracao();
        this.tipo = aviso.getTipo();
        this.nomeMinisterio = aviso.getMinisterio() != null ? aviso.getMinisterio().getName() : null;
        this.nomeAutor = aviso.getAutor() != null ? aviso.getAutor().getName() : null;
    }
}