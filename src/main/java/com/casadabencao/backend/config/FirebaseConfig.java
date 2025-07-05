package com.casadabencao.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            InputStream serviceAccount =
                getClass().getClassLoader().getResourceAsStream("firebase/firebase-key.json");

            if (serviceAccount == null) {
                throw new RuntimeException("Arquivo firebase-key.json não encontrado em resources/firebase");
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase inicializado com sucesso");
            }

        } catch (IOException e) {
            System.err.println("❌ Erro ao inicializar o Firebase: " + e.getMessage());
        }
    }
}
