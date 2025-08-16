package com.fintech.notification.service;

import com.fintech.notification.dto.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String destination, NotificationMessage message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    public void sendNotificationToUser(Long userId, NotificationMessage message) {
        // For user-specific notifications, we can use a topic like /topic/user/{userId}/notifications
        messagingTemplate.convertAndSend("/topic/user/" + userId + "/notifications", message);
    }

    public void sendFraudAlertToAdmins(NotificationMessage message) {
        // For admin-specific fraud alerts, we can use a topic like /topic/admin/fraud-alerts
        messagingTemplate.convertAndSend("/topic/admin/fraud-alerts", message);
    }
}
