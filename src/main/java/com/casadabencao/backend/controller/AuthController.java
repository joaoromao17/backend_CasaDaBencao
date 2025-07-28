package com.casadabencao.backend.controller;

import com.casadabencao.backend.dto.LoginRequest;
import com.casadabencao.backend.dto.RefreshTokenRequest;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.UsuarioRepository;
import com.casadabencao.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        List<String> roles = toRoleList(usuario);

        String accessToken = jwtUtil.generateAccessToken(usuario.getId(), usuario.getEmail(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(usuario.getId(), usuario.getEmail(), roles);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }

        try {
            String email = jwtUtil.getEmailFromToken(refreshToken);
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            List<String> roles = jwtUtil.getRolesFromToken(refreshToken);

            String newAccessToken = jwtUtil.generateAccessToken(userId, email, roles);
            return ResponseEntity.ok(newAccessToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao renovar token");
        }
    }

    private List<String> toRoleList(Usuario u) {
        return u.getRoles().stream().map(Enum::name).collect(Collectors.toList());
    }
}