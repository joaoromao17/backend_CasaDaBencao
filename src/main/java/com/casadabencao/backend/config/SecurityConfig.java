package com.casadabencao.backend.config;

import com.casadabencao.backend.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
                        })
                )
                .authorizeHttpRequests(auth -> auth

                        // === ROTAS PÚBLICAS ===
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/users/forgot-password",
                                "/api/users/reset-password",
                                "/api/versiculos/**",
                                "/api/eventos/**",
                                "/api/testemunhos/**",
                                "/api/oracoes/**",
                                "/api/ministerios",
                                "/api/ministerios/**",
                                "/api/estudos",
                                "/api/contribuicoes",
                                "/api/users",
                                "/api/contribuicoes/**",
                                "/api/users/public/roles/**",
                                "/api/users/public/membros",
                                "/api/users/public/aniversariantes"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/*/public").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/oracoes", "/api/oracoes/*", "/api/oracoes/pedidos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/testemunhos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/ministerios/**").permitAll()

                        // === ROTAS PARA USUÁRIOS AUTENTICADOS ===
                        .requestMatchers(
                                "/api/users/me",
                                "/api/users/profile",
                                "/api/users/password",
                                "/api/contribuicoes/*"
                        ).authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/fcm-token").authenticated() // 🔐 Protege FCM corretamente
                        .requestMatchers(HttpMethod.DELETE, "/api/users/profile").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/oracoes/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/oracoes/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/oracoes/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/oracoes/minhas").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/testemunhos/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/testemunhos/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/testemunhos/**").authenticated()
                        
                        // Permitimos apenas se houver endpoints públicos específicos para PUT (ex: atualização de info pública)
                        .requestMatchers(HttpMethod.PUT, "/api/users/public/**").permitAll()

                        // === PERMISSÕES ADMIN (POST, PUT, DELETE) ===

                        .requestMatchers(HttpMethod.POST, "/api/ministerios/**").hasAnyRole("ADMIN", "PASTOR")
                        .requestMatchers(HttpMethod.POST, "/api/ministerios").hasAnyRole("ADMIN", "PASTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/ministerios/**").hasAnyRole("ADMIN", "PASTOR")

                        .requestMatchers(HttpMethod.POST, "/api/estudos/**").hasAnyRole("ADMIN", "PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/api/estudos/**").hasAnyRole("ADMIN", "PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/estudos/**").hasAnyRole("ADMIN", "PROFESSOR")

                        .requestMatchers(HttpMethod.POST, "/api/eventos/**").hasAnyRole("ADMIN", "PASTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/eventos/**").hasAnyRole("ADMIN", "PASTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/eventos/**").hasAnyRole("ADMIN", "PASTOR")

                        .requestMatchers(HttpMethod.POST, "/api/contribuicoes/**").hasAnyRole("ADMIN", "LIDER", "PASTOR", "PASTORAUXILIAR")
                        .requestMatchers(HttpMethod.PUT, "/api/contribuicoes/**").hasAnyRole("ADMIN", "LIDER", "PASTOR", "PASTORAUXILIAR")
                        .requestMatchers(HttpMethod.DELETE, "/api/contribuicoes/**").hasAnyRole("ADMIN", "LIDER", "PASTOR", "PASTORAUXILIAR")

                        .requestMatchers(HttpMethod.POST, "/api/testemunhos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/testemunhos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/testemunhos/**").hasRole("ADMIN")

                        // Usuários - ADMIN pode tudo
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")


                        // Endpoints públicos (sem ID)
                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/versiculos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/versiculos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/versiculos/**").hasRole("ADMIN")

                        // === ROTAS DE ADMIN EXTRA ===
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // === QUALQUER OUTRA REQUISIÇÃO EXIGE LOGIN ===
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
