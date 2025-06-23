package com.casadabencao.backend.controller;

import com.casadabencao.backend.dto.LoginRequest;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.UsuarioRepository;
import com.casadabencao.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        return jwtUtil.generateToken(
                usuario.getId(), // ✅ agora inclui o ID
                usuario.getEmail(),
                usuario.getRoles().stream()
                        .map(Enum::name)
                        .toList()
        );
    }
}
