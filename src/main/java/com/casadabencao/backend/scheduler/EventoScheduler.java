package com.casadabencao.backend.scheduler;

import com.casadabencao.backend.model.Evento;
import com.casadabencao.backend.repository.EventoRepository;
import com.casadabencao.backend.repository.UsuarioRepository;
import com.casadabencao.backend.service.FirebaseService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EventoScheduler {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FirebaseService firebaseService;

    public EventoScheduler(EventoRepository eventoRepository, UsuarioRepository usuarioRepository, FirebaseService firebaseService) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.firebaseService = firebaseService;
    }

    // Roda todo dia às 8h da manhã
    @Scheduled(cron = "0 0 8 * * *", zone = "America/Sao_Paulo")
    public void notificarEventosDoDia() {
        LocalDate hoje = LocalDate.now();
        List<Evento> eventosDoDia = eventoRepository.findByDate(hoje);

        for (Evento evento : eventosDoDia) {
            String titulo = "📣 É HOJE, JÁ FIQUE ATENTO!";
            String corpo = evento.getTitle();
            String link = "https://localhost/eventos/" + evento.getId();

            usuarioRepository.findAll().forEach(usuario -> {
                String token = usuario.getFcmToken();
                if (token != null && !token.isBlank()) {
                    firebaseService.enviarNotificacaoComLink(titulo, corpo, token, link);
                }
            });
        }

        if (!eventosDoDia.isEmpty()) {
            System.out.println("✅ Notificações de evento enviadas para hoje: " + hoje);
        }
    }
}
