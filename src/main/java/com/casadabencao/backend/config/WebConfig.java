package com.casadabencao.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @PostConstruct
    public void logCorsInit() {
        System.out.println("✅ CORS config carregada com suporte a:");
        System.out.println("   - http://localhost:3000");
        System.out.println("   - https://casa-da-ben.vercel.app");
        System.out.println("   - capacitor://localhost");
        System.out.println("   - http://localhost");
        System.out.println("   - https://localhost");
        System.out.println("   - https://localhost/");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:uploads/profiles/");

        registry.addResourceHandler("/uploads/pdfs/**")
                .addResourceLocations("file:uploads/pdfs/");

        registry.addResourceHandler("/uploads/ministerios/**")
                .addResourceLocations("file:uploads/ministerios/");

        registry.addResourceHandler("/uploads/contribuicoes/**")
                .addResourceLocations("file:uploads/contribuicoes/");

        registry.addResourceHandler("/uploads/eventos/**")
                .addResourceLocations("file:uploads/eventos/");

        registry.addResourceHandler("/uploads/avisos/**")
                .addResourceLocations("file:uploads/avisos/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "https://casa-da-ben.vercel.app",
                        "capacitor://localhost",
                        "http://localhost",
                        "https://localhost/",
                        "https://localhost"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
