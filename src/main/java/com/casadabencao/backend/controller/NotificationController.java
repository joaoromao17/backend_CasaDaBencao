package com.casadabencao.backend.controller;

import com.casadabencao.backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/test")
    public ResponseEntity<?> testNotification(@RequestBody TestNotificationRequest request) {
        notificationService.sendPushNotification(request.getFcmToken(), request.getTitle(), request.getBody());
        return ResponseEntity.ok("Notificação enviada!");
    }

    // Classe interna para receber o corpo da requisição
    public static class TestNotificationRequest {
        private String fcmToken;
        private String title;
        private String body;

        // Getters e Setters
        public String getFcmToken() { return fcmToken; }
        public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }
}
