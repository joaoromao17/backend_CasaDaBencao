package com.casadabencao.backend.service;

// Service - AvisoService.java
import com.casadabencao.backend.dto.AvisoDto;
import com.casadabencao.backend.dto.NovoAvisoDto;
import com.casadabencao.backend.model.Aviso;
import com.casadabencao.backend.model.TipoAviso;
import com.casadabencao.backend.repository.AvisoRepository;
import com.casadabencao.backend.repository.MinisterioRepository;
import com.casadabencao.backend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;

@Service
public class AvisoService {

    private final AvisoRepository repository;
    private final MinisterioRepository ministerioRepository;
    private final UsuarioRepository usuarioRepository;

    public AvisoService(AvisoRepository repository, MinisterioRepository ministerioRepository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.ministerioRepository = ministerioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public AvisoDto criar(NovoAvisoDto dto, Long autorId) {
        Aviso aviso = new Aviso();
        aviso.setTitulo(dto.getTitulo());
        aviso.setMensagem(dto.getMensagem());
        aviso.setArquivoUrl(dto.getArquivoUrl());
        aviso.setDataExpiracao(dto.getDataExpiracao());
        aviso.setTipo(dto.getTipo());

        if (dto.getTipo() == TipoAviso.MINISTERIAL) {
            if (dto.getMinisterioId() == null) {
                throw new IllegalArgumentException("MinisterioId obrigatório para avisos ministeriais.");
            }
            aviso.setMinisterio(ministerioRepository.findById(dto.getMinisterioId()).orElseThrow(() -> new EntityNotFoundException("Ministério não encontrado")));
        }

        aviso.setAutor(usuarioRepository.findById(autorId).orElse(null));

        return new AvisoDto(repository.save(aviso));
    }

    public List<AvisoDto> listarAvisosVisiveis() {
        LocalDate hoje = LocalDate.now();
        return repository.findByDataExpiracaoIsNullOrDataExpiracaoAfter(hoje)
                .stream().map(AvisoDto::new).collect(Collectors.toList());
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
