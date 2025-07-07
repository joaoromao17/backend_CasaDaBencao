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

    // 🔔 Notificação simples
    public void enviarNotificacao(String titulo, String corpo, String token) {
        Notification notification = Notification.builder()
                .setTitle(titulo)
                .setBody(corpo)
                .build();

        AndroidNotification androidNotification = AndroidNotification.builder()
                .setIcon("ic_notification") // imagem 96x96 em res/drawable
                .setSound("default")
                .setPriority(AndroidNotification.Priority.HIGH)
                .build();

        AndroidConfig androidConfig = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
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

    // 🔔 Notificação com redirecionamento (link)
    public void enviarNotificacaoComLink(String titulo, String corpo, String token, String link) {
        Notification notification = Notification.builder()
                .setTitle(titulo)
                .setBody(corpo)
                .build();

        AndroidNotification androidNotification = AndroidNotification.builder()
                .setIcon("ic_notification")
                .setSound("default")
                .setPriority(AndroidNotification.Priority.HIGH)
                .build();

        AndroidConfig androidConfig = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(androidNotification)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .setAndroidConfig(androidConfig)
                .putData("click_action", link) // 👈 redirecionamento no app
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Notificação com link enviada com sucesso: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("❌ Erro ao enviar notificação com link: " + e.getMessage());
        }
    }
}
