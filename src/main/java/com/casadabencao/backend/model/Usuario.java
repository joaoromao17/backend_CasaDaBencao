package com.casadabencao.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import com.casadabencao.backend.model.Ministerio;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    @Column(nullable = false)
    private Boolean member;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private List<Role> roles = new ArrayList<>();

    private String address;
    private String birthDate;
    private String maritalStatus;

    @Column(nullable = false)
    private Boolean baptized;

    @Column(nullable = false)
    private Boolean acceptedTerms;

    @Column(nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "usuario_ministerio",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "ministerio_id")
    )
    @JsonIgnoreProperties({"leaders", "viceLeaders", "ministries"})
    private List<Ministerio> ministries = new ArrayList<>();

    @Column(length = 512)
    private String profileImageUrl;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(length = 512)
private String fcmToken;


}
