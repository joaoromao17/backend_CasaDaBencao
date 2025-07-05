package com.casadabencao.backend.service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    public void enviarNotificacao(String titulo, String corpo, String token) {
        Notification notification = Notification.builder()
                .setTitle(titulo)
                .setBody(corpo)
                .build();

        AndroidNotification androidNotification = AndroidNotification.builder()
                .setIcon("ic_notification") // Nome do ícone sem a extensão .png
                .setSound("default") // Toque padrão
                .setColor("#ffffff") // Cor do ícone (opcional)
                .build();

        AndroidConfig androidConfig = AndroidConfig.builder()
                .setNotification(androidNotification)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .setAndroidConfig(androidConfig)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Notificação enviada com sucesso: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("❌ Erro ao enviar notificação: " + e.getMessage());
        }
    }
}
