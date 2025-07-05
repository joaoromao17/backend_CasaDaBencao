package com.casadabencao.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
        String base64 = System.getenv("FIREBASE_CREDENTIALS_BASE64");
        if (base64 == null || base64.isEmpty()) {
            throw new RuntimeException("⚠️ FIREBASE_CREDENTIALS_BASE64 não encontrada");
        }

        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(decodedBytes));

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase App inicializado com sucesso via variável de ambiente!");
        }
    }
}
