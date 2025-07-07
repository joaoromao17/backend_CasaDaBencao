package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.model.Versiculo;
import com.casadabencao.backend.repository.UsuarioRepository;
import com.casadabencao.backend.repository.VersiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.io.IOException;

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
        LocalDate hoje = LocalDate.now();

        if (dataUltimaAtualizacao == null || !hoje.equals(dataUltimaAtualizacao)) {
            List<Versiculo> todos = versiculoRepository.findAll();
            if (todos.isEmpty()) {
                throw new RuntimeException("Nenhum versículo disponível no banco de dados.");
            }

            versiculoDoDia = todos.get(new Random().nextInt(todos.size()));
            dataUltimaAtualizacao = hoje;

            // 🔔 Enviar notificação para todos os usuários com FCM
            String mensagem = "\"" + versiculoDoDia.getVerse() + "\" — " + versiculoDoDia.getReference();

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

        return versiculoDoDia;
    }

}
