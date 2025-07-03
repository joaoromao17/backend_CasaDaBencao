package com.casadabencao.backend.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    public void enviarNotificacao(String titulo, String corpo, String token) {
        Notification notification = Notification.builder()
                .setTitle(titulo)
                .setBody(corpo)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Notificação enviada com sucesso: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("❌ Erro ao enviar notificação: " + e.getMessage());
        }
    }
}
