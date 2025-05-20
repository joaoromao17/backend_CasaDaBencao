package com.casadabencao.backend.config;

import com.casadabencao.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.Customizer;

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
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
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
                                "/api/estudos",
                                "/api/contribuicoes",
                                "/api/users"
                        ).permitAll()

                        // === ROTAS PARA USUÁRIOS AUTENTICADOS ===
                        .requestMatchers(
                                "/api/users/me",
                                "/api/users/profile",
                                "/api/users/password",
                                "/api/contribuicoes/*",
                                "/api/ministerios/*",
                                "/api/estudos/*/pdf"
                        ).authenticated()

                        // === ROTAS COM PERMISSÕES ESPECÍFICAS ===
                        .requestMatchers(HttpMethod.POST, "/api/ministerios").hasAnyRole("PASTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/ministerios/**").hasAnyRole("LIDER", "PASTOR", "ADMIN")

                        .requestMatchers("/api/estudos/**").hasAnyRole("PROFESSOR", "PASTOR", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/eventos").hasAnyRole("LIDER", "PASTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/eventos/**").hasAnyRole("LIDER", "PASTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/eventos/**").hasAnyRole("LIDER", "PASTOR", "ADMIN")

                        .requestMatchers("/api/contribuicoes/criar").hasAnyRole("LIDER", "PASTOR", "ADMIN")

                        .requestMatchers("/api/admin/**").hasAnyRole("LIDER", "PASTOR", "ADMIN")

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
