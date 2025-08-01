package com.casadabencao.backend.service;

import com.casadabencao.backend.dto.EventoDto;
import com.casadabencao.backend.model.Evento;
import com.casadabencao.backend.repository.EventoRepository;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.casadabencao.backend.service.CloudinaryService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.io.IOException;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private UsuarioRepository usuarioRepository;


    public List<Evento> findAll() {
        return eventoRepository.findAll();
    }

    public List<Evento> findUpcoming() {
        LocalDate hoje = LocalDate.now();
        return eventoRepository.findByDateGreaterThanEqualOrderByDateAsc(hoje);
    }

    public Optional<Evento> findById(Long id) {
        return eventoRepository.findById(id);
    }

    public Evento saveFromDto(EventoDto dto, MultipartFile image) throws IOException {
        Evento evento = new Evento();
        evento.setTitle(dto.getTitle());
        evento.setDescription(dto.getDescription());
        evento.setDate(dto.getDate());
        evento.setTime(LocalTime.parse(dto.getTime()));
        evento.setLocation(dto.getLocation());
        evento.setCategory(dto.getCategory());

        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(image, "eventos");
            evento.setImageUrl(imageUrl);
        }

        evento = eventoRepository.save(evento);

        // 🔔 Enviar notificação para todos os usuários com FCM
        String tituloNotificacao = "📅 Novo evento da ICB! NÃO PERCA!";
        String corpoNotificacao = evento.getTitle();
        String link = "https://localhost/eventos/" + evento.getId();

        usuarioRepository.findAll().forEach(usuario -> {
            String token = usuario.getFcmToken();
            if (token != null && !token.isBlank()) {
                firebaseService.enviarNotificacaoComLink(tituloNotificacao, corpoNotificacao, token, link);
            }
        });

        return evento;
    }

    public Evento updateFromDto(Long id, EventoDto dto, MultipartFile image) throws IOException {
        return eventoRepository.findById(id).map(evento -> {
            evento.setTitle(dto.getTitle());
            evento.setDescription(dto.getDescription());
            evento.setDate(dto.getDate());
            evento.setTime(LocalTime.parse(dto.getTime()));
            evento.setLocation(dto.getLocation());
            evento.setCategory(dto.getCategory());

            try {
                if (image != null && !image.isEmpty()) {
                    String imageUrl = cloudinaryService.uploadFile(image, "eventos");
                    evento.setImageUrl(imageUrl);
                }
            } catch (IOException e) {
                throw new RuntimeException("Erro ao fazer upload da imagem para o Cloudinary", e);
            }

            return eventoRepository.save(evento);
        }).orElse(null);
    }

    public void delete(Long id) {
        eventoRepository.deleteById(id);
    }
}
