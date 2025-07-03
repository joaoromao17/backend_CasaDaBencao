package com.casadabencao.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

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
                "https://localhost" // <-- ADICIONE ESTA LINHA
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
}
}
