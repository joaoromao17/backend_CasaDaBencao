package com.casadabencao.backend.service;

import com.casadabencao.backend.dto.UsuarioDto;
import com.casadabencao.backend.model.Ministerio;
import com.casadabencao.backend.model.PasswordResetToken;
import com.casadabencao.backend.model.Role;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.MinisterioRepository;
import com.casadabencao.backend.repository.PasswordResetTokenRepository;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import com.casadabencao.backend.service.CloudinaryService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;

@Service
public class UsuarioService {

    private static final String DEFAULT_PROFILE_IMAGE = "https://res.cloudinary.com/dew6zhisa/image/upload/v1750872463/default-profile_crggbc.jpg";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FirebaseService firebaseService;

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
        // Normaliza email
        String emailNormalizado = usuario.getEmail().toLowerCase();

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        usuario.setEmail(emailNormalizado);

        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            if (Boolean.TRUE.equals(usuario.getMember())) {
                usuario.setRoles(List.of(Role.ROLE_MEMBRO));
            } else {
                usuario.setRoles(List.of(Role.ROLE_VISITANTE));
            }
        }

        if (usuario.getRoles().contains(Role.ROLE_LIDER) &&
                (usuario.getMinistries() == null || usuario.getMinistries().isEmpty())) {
            throw new IllegalArgumentException("Usuários com cargo de LIDER devem estar associados a pelo menos um ministério.");
        }

        if (usuario.getMinistries() != null) {
            List<Ministerio> ministries = usuario.getMinistries().stream()
                    .map(m -> ministerioRepository.findById(m.getId()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            usuario.setMinistries(ministries);
        }

        if (usuario.getProfileImageUrl() == null || usuario.getProfileImageUrl().isBlank()) {
            usuario.setProfileImageUrl(DEFAULT_PROFILE_IMAGE);
        }

        Usuario saved = usuarioRepository.save(usuario);

        // 🔔 Notificar admins com FCM
        List<Usuario> admins = usuarioRepository.findByRolesContaining(Role.ROLE_ADMIN);
        for (Usuario admin : admins) {
            String fcm = admin.getFcmToken();
            if (fcm != null && !fcm.isBlank()) {
                firebaseService.enviarNotificacaoComLink(
                        "🆕 Novo usuário na área!",
                        saved.getName(),
                        fcm,
                        "https://localhost/usuarios"
                );
            }
        }

        return saved;
    }

public Usuario update(Long id, Usuario updated) {
    return usuarioRepository.findById(id).map(usuario -> {

        if (updated.getName() != null) usuario.setName(updated.getName());
        if (updated.getPhone() != null) usuario.setPhone(updated.getPhone());

        if (updated.getEmail() != null) {
            String novoEmail = updated.getEmail().toLowerCase();
            Optional<Usuario> outroUsuario = usuarioRepository.findByEmail(novoEmail);
            if (outroUsuario.isPresent() && !outroUsuario.get().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Este email já está em uso.");
            }
            usuario.setEmail(novoEmail);
        }

        if (updated.getMember() != null) usuario.setMember(updated.getMember());
        if (updated.getAddress() != null) usuario.setAddress(updated.getAddress());
        if (updated.getBirthDate() != null) usuario.setBirthDate(updated.getBirthDate());
        if (updated.getMaritalStatus() != null) usuario.setMaritalStatus(updated.getMaritalStatus());
        if (updated.getBaptized() != null) usuario.setBaptized(updated.getBaptized());
        if (updated.getAcceptedTerms() != null) usuario.setAcceptedTerms(updated.getAcceptedTerms());
        if (updated.getProfileImageUrl() != null) usuario.setProfileImageUrl(updated.getProfileImageUrl());

        if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
            if (!passwordEncoder.matches(updated.getPassword(), usuario.getPassword())) {
                usuario.setPassword(passwordEncoder.encode(updated.getPassword()));
            }
        }

        if (updated.getRoles() != null) usuario.setRoles(updated.getRoles());

        if (updated.getMinistries() != null) {
            List<Ministerio> ministries = updated.getMinistries().stream()
                    .map(m -> ministerioRepository.findById(m.getId()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            usuario.setMinistries(ministries);
        }

        if (updated.getBiography() != null) usuario.setBiography(updated.getBiography());

        return usuarioRepository.save(usuario);
    }).orElse(null);
}

    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }

public Usuario findByEmail(String email) {
    // Sempre busca email em minúsculas
    return usuarioRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
}



    public void updateProfileImage(MultipartFile file, String email) {
    }

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

public void sendResetToken(String email) {
    // Normaliza email antes da busca
    Usuario usuario = usuarioRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    String token = UUID.randomUUID().toString();
    PasswordResetToken resetToken = new PasswordResetToken();
    resetToken.setToken(token);
    resetToken.setUsuario(usuario);
    resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));
    tokenRepository.save(resetToken);

    String resetLink = "https://casa-da-ben.vercel.app/reset-password?token=" + token;

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

    public List<Usuario> findAllById(List<Long> ids) {
        return usuarioRepository.findAllById(ids);
    }

    public List<Usuario> findByRole(Role role) {
        return usuarioRepository.findByRolesContaining(role);
    }

    public Page<Usuario> findMembros(Pageable pageable) {
        return usuarioRepository.findByRoleOrdered(Role.ROLE_MEMBRO, pageable);
    }

    public List<Usuario> findAniversariantesDoMes() {
        Month mesAtual = LocalDate.now().getMonth();
        return usuarioRepository.findAll().stream()
                .filter(u -> {
                    try {
                        LocalDate birth = LocalDate.parse(u.getBirthDate());
                        return birth.getMonth().equals(mesAtual);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .sorted(Comparator.comparing(u -> {
                    LocalDate birth = LocalDate.parse(u.getBirthDate());
                    return birth.withYear(LocalDate.now().getYear());
                }))
                .collect(Collectors.toList());
    }

    public Page<Usuario> findMembros(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return usuarioRepository.findByRoleOrdered(Role.ROLE_MEMBRO, pageable);
        }
        return usuarioRepository.findByRoleAndNameContainingIgnoreCase(Role.ROLE_MEMBRO, search.trim(), pageable);
    }

    public void atualizarFcmToken(String token, String email) {
    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

    usuario.setFcmToken(token);
    usuarioRepository.save(usuario);
}
}
