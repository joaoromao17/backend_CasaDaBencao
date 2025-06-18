package com.casadabencao.backend.dto;

import com.casadabencao.backend.model.Ministerio;
import com.casadabencao.backend.model.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UsuarioPublicDto {
    private Long id;
    private String name;
    private String phone;
    private String biography;
    private String profileImageUrl;
    private List<String> roles;
    private List<String> ministerios;
    private String birthDate;

    public UsuarioPublicDto(Usuario usuario) {
        this.id = usuario.getId();
        this.name = usuario.getName();
        this.phone = usuario.getPhone();
        this.biography = usuario.getBiography();
        this.birthDate = usuario.getBirthDate();
        this.profileImageUrl = usuario.getProfileImageUrl(); // 👈 NOVO
        this.roles = usuario.getRoles().stream()
                .map(role -> role.name().replace("ROLE_", "").toLowerCase())
                .collect(Collectors.toList());
        this.ministerios = usuario.getMinistries().stream()
                .map(m -> m.getName())
                .collect(Collectors.toList());
    }
}
