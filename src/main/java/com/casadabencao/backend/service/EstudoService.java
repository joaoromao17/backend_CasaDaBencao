package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Estudo;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.EstudoRepository;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EstudoService {

    @Autowired
    private EstudoRepository estudoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FirebaseService firebaseService;

    public List<Estudo> findAll() {
        return estudoRepository.findAll();
    }

    public Optional<Estudo> findById(Long id) {
        return estudoRepository.findById(id);
    }

    public Estudo save(Estudo estudo) {
        Estudo salvo = estudoRepository.save(estudo);

        // 🔔 Enviar notificação para todos os usuários com FCM token
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            String fcm = usuario.getFcmToken();
            if (fcm != null && !fcm.isEmpty()) {
                firebaseService.enviarNotificacaoComLink(
                        "📘 Estudo bíblico novo no ar!",
                        "Tema: " + estudo.getTitle(),
                        fcm,
                        "https://localhost/estudos"
                );
            }
        }

        return salvo;
    }

    public void deleteById(Long id) {
        estudoRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return estudoRepository.existsById(id);
    }
}
