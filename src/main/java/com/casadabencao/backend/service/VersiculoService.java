package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.model.Versiculo;
import com.casadabencao.backend.repository.UsuarioRepository;
import com.casadabencao.backend.repository.VersiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class VersiculoService {

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final VersiculoRepository versiculoRepository;
    private Versiculo versiculoDoDia;
    private LocalDate dataUltimaAtualizacao;

    @Autowired
    public VersiculoService(VersiculoRepository versiculoRepository) {
        this.versiculoRepository = versiculoRepository;
        this.dataUltimaAtualizacao = null;
    }

    public Versiculo getVersiculoDoDia() {
        return versiculoDoDia;
    }

    // 🕗 Agendado para rodar todos os dias às 08:00 da manhã
    @Scheduled(cron = "0 0 8 * * *") // formato: segundo, minuto, hora, dia, mês, dia-da-semana
    public void atualizarVersiculoDoDia() {
        LocalDate hoje = LocalDate.now();

        if (dataUltimaAtualizacao == null || !hoje.equals(dataUltimaAtualizacao)) {
            List<Versiculo> todos = versiculoRepository.findAll();
            if (todos.isEmpty()) {
                System.out.println("⚠️ Nenhum versículo disponível no banco.");
                return;
            }

            versiculoDoDia = todos.get(new Random().nextInt(todos.size()));
            dataUltimaAtualizacao = hoje;

            String mensagem = "\"" + versiculoDoDia.getVerse() + "\" — " + versiculoDoDia.getReference();
            System.out.println("📖 Novo versículo do dia definido: " + mensagem);

            List<Usuario> usuarios = usuarioRepository.findAll();
            for (Usuario usuario : usuarios) {
                String fcm = usuario.getFcmToken();
                if (fcm != null && !fcm.isBlank()) {
                    firebaseService.enviarNotificacaoComLink(
                            "📖 Versículo do dia!",
                            mensagem,
                            fcm,
                            "https://localhost"
                    );
                }
            }
        }
    }
}