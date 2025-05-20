package com.casadabencao.backend.service;

import com.casadabencao.backend.model.Ministerio;
import com.casadabencao.backend.model.PasswordResetToken;
import com.casadabencao.backend.model.Role;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.MinisterioRepository;
import com.casadabencao.backend.repository.PasswordResetTokenRepository;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean checkPassword(String rawPassword, Usuario usuario) {
        return passwordEncoder.matches(rawPassword, usuario.getPassword());
    }

    public void changePassword(Usuario usuario, String newPassword) {
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    @Autowired
    private MinisterioRepository ministerioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        // Define cargos iniciais com base no campo "member"
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            if (Boolean.TRUE.equals(usuario.getMember())) {
                usuario.setRoles(List.of(Role.MEMBRO));
            } else {
                usuario.setRoles(List.of(Role.VISITANTE));
            }
        }

        // Valida se for LIDER, precisa de ministérios
        if (usuario.getRoles().contains(Role.LIDER) &&
                (usuario.getMinistries() == null || usuario.getMinistries().isEmpty())) {
            throw new IllegalArgumentException("Usuários com cargo de LIDER devem estar associados a pelo menos um ministério.");
        }

        // Vincula ministérios existentes
        if (usuario.getMinistries() != null) {
            List<Ministerio> ministries = usuario.getMinistries().stream()
                    .map(m -> ministerioRepository.findById(m.getId()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            usuario.setMinistries(ministries);
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario update(Long id, Usuario updated) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setName(updated.getName());
            usuario.setPhone(updated.getPhone());
            Optional<Usuario> outroUsuario = usuarioRepository.findByEmail(updated.getEmail());
            if (outroUsuario.isPresent() && !outroUsuario.get().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Este email já está em uso.");
            }
            usuario.setEmail(updated.getEmail());
            usuario.setMember(updated.getMember());
            usuario.setAddress(updated.getAddress());
            usuario.setBirthDate(updated.getBirthDate());
            usuario.setMaritalStatus(updated.getMaritalStatus());
            usuario.setBaptized(updated.getBaptized());
            usuario.setAcceptedTerms(updated.getAcceptedTerms());
            usuario.setProfileImageUrl(updated.getProfileImageUrl());

            if (updated.getPassword() != null && !updated.getPassword().isEmpty()
                    && !passwordEncoder.matches(updated.getPassword(), usuario.getPassword())) {
                usuario.setPassword(passwordEncoder.encode(updated.getPassword()));
            }

            if (updated.getRoles() != null) {
                usuario.setRoles(updated.getRoles());

                if (updated.getRoles().contains(Role.LIDER) &&
                        (updated.getMinistries() == null || updated.getMinistries().isEmpty())) {
                    throw new IllegalArgumentException("Usuários com cargo de LIDER devem estar associados a pelo menos um ministério.");
                }
            }

            if (updated.getMinistries() != null) {
                List<Ministerio> ministries = updated.getMinistries().stream()
                        .map(m -> ministerioRepository.findById(m.getId()).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                usuario.setMinistries(ministries);
            }

            return usuarioRepository.save(usuario);
        }).orElse(null);
    }

    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }


    public void updateProfileImage(MultipartFile file, String email) {
    }

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetToken(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Redefinição de Senha");
        mail.setText("Para redefinir sua senha, clique no link: " + resetLink);
        mail.setFrom("icbcasadabencao610@gmail.com");
        mailSender.send(mail);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        tokenRepository.delete(resetToken); // consome o token
    }


}