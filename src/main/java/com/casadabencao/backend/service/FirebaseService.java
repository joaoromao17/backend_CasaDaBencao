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
                .setIcon("ic_notification") // nome da imagem na pasta res/drawable
                .setSound("default") // som padrão
                .setPriority(AndroidNotification.Priority.HIGH) // prioridade máxima
                .build();

        AndroidConfig androidConfig = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH) // notificação de alta prioridade
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
