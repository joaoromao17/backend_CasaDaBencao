package com.casadabencao.backend.config;

import com.casadabencao.backend.model.Ministerio;
import com.casadabencao.backend.model.Role;
import com.casadabencao.backend.model.Usuario;
import com.casadabencao.backend.repository.MinisterioRepository;
import com.casadabencao.backend.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner initAdmin(
            UsuarioRepository usuarioRepository,
            MinisterioRepository ministerioRepository,
            PasswordEncoder passwordEncoder // Lembre-se de ter esse bean configurado
    ) {
        return args -> {
            String adminEmail = "icbcasadabencao610@gmail.com";

            if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
                List<Ministerio> todosMinisterios = ministerioRepository.findAll();

                Usuario admin = new Usuario();
                admin.setName("Administrador Casa Da Benção");
                admin.setEmail(adminEmail);
                admin.setPhone("(61) 98614-9855");
                admin.setMember(true);
                admin.setRoles(List.of(Role. ROLE_ADMIN));
                admin.setAddress("Qs610");
                admin.setBirthDate("01/01/2000");
                admin.setMaritalStatus("casado(a)");
                admin.setBaptized(true);
                admin.setAcceptedTerms(true);
                admin.setPassword(passwordEncoder.encode("admin123")); // senha criptografada
                admin.setMinistries(todosMinisterios);
                admin.setProfileImageUrl("/uploads/profiles/profile_admin.jpg");

                usuarioRepository.save(admin);
                System.out.println("👑 Conta de administrador criada com sucesso.");
            } else {
                System.out.println("✅ Conta de administrador já existe.");
            }
        };
    }
}
