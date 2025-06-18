package com.casadabencao.backend.repository;

import com.casadabencao.backend.model.Role;
import com.casadabencao.backend.model.Usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByMinistries_Id(Long ministerioId);
    List<Usuario> findByRolesContaining(Role role);

    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r = :role ORDER BY u.name ASC")
    Page<Usuario> findByRoleOrdered(@Param("role") Role role, Pageable pageable);

    @Query("SELECT u FROM Usuario u JOIN u.roles r " +
            "WHERE r = :role AND LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY u.name ASC")
    Page<Usuario> findByRoleAndNameContainingIgnoreCase(
            @Param("role") Role role,
            @Param("search") String search,
            Pageable pageable
    );
}
