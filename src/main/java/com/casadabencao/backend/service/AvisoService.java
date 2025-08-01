package com.casadabencao.backend.service;

import com.casadabencao.backend.dto.AvisoDto;
import com.casadabencao.backend.dto.NovoAvisoDto;
import com.casadabencao.backend.model.Aviso;
import com.casadabencao.backend.model.TipoAviso;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.AvisoRepository;
import com.casadabencao.backend.repository.MinisterioRepository;
import com.casadabencao.backend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import com.casadabencao.backend.util.HtmlSanitizer;

@Service
public class AvisoService {

    private final AvisoRepository repository;
    private final MinisterioRepository ministerioRepository;
    private final UsuarioRepository usuarioRepository;
    private final FirebaseService firebaseService;

    @Autowired
    public AvisoService(
        AvisoRepository repository,
        MinisterioRepository ministerioRepository,
        UsuarioRepository usuarioRepository,
        FirebaseService firebaseService
    ) {
        this.repository = repository;
        this.ministerioRepository = ministerioRepository;
        this.usuarioRepository = usuarioRepository;
        this.firebaseService = firebaseService;
    }

    public AvisoDto criar(NovoAvisoDto dto, Long autorId) {
        Aviso aviso = new Aviso();
        aviso.setTitulo(dto.getTitulo());
        aviso.setMensagem(HtmlSanitizer.sanitize(dto.getMensagem()));
        aviso.setArquivoUrl(dto.getArquivoUrl());
        aviso.setDataExpiracao(dto.getDataExpiracao());
        aviso.setTipo(dto.getTipo());
    
        List<Usuario> usuariosParaNotificar;
    
        String tituloNotificacao;
    
        if (dto.getTipo() == TipoAviso.MINISTERIAL) {
            if (dto.getMinisterioId() == null) {
                throw new IllegalArgumentException("MinisterioId obrigatório para avisos ministeriais.");
            }
    
            aviso.setMinisterio(ministerioRepository.findById(dto.getMinisterioId())
                    .orElseThrow(() -> new EntityNotFoundException("Ministério não encontrado")));
    
            usuariosParaNotificar = usuarioRepository.findByMinistries_Id(dto.getMinisterioId());
    
            // Título personalizado para ministério
            tituloNotificacao = "📢 Novo aviso do " + aviso.getMinisterio().getName() + "!";
        } else {
            usuariosParaNotificar = usuarioRepository.findAll();
            tituloNotificacao = "📢 Novo aviso da ICB!";
        }
    
        aviso.setAutor(usuarioRepository.findById(autorId).orElse(null));
        aviso = repository.save(aviso);
    
        // 🔔 Enviar notificação
        for (Usuario usuario : usuariosParaNotificar) {
            String fcm = usuario.getFcmToken();
            if (fcm != null && !fcm.isEmpty()) {
                firebaseService.enviarNotificacao(
                    tituloNotificacao,
                    aviso.getTitulo(),
                    fcm
                );
            }
        }
    
        return new AvisoDto(aviso);
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
