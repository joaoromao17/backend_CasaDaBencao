package com.casadabencao.backend.dto;

import com.casadabencao.backend.model.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
public class UsuarioDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private List<String> roles;
    private String profileImageUrl;
    private String biography;

    public UsuarioDto(Usuario usuario) {
        this.id = usuario.getId();
        this.name = usuario.getName();
        this.email = usuario.getEmail();
        this.phone = usuario.getPhone();
        this.roles = usuario.getRoles().stream()
                .map(role -> role.name().replace("ROLE_", "").toLowerCase())
                .collect(Collectors.toList());
        this.profileImageUrl = usuario.getProfileImageUrl();
        this.biography = usuario.getBiography();
    }
}
