package com.casadabencao.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseInitializer {

    @PostConstruct
    public void initialize() throws IOException {
        try (InputStream serviceAccount = getClass().getResourceAsStream("/firebase/icb-610-firebase-adminsdk-fbsvc-99bd49f188.json")) {
            if (serviceAccount == null) {
                throw new IOException("❌ Arquivo de credencial do Firebase não encontrado em /firebase/");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase inicializado com sucesso!");
            } else {
                System.out.println("⚠️ Firebase já estava inicializado.");
            }
        }
    }
}
