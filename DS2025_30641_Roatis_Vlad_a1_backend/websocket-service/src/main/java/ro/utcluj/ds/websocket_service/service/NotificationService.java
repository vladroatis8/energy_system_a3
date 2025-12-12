package ro.utcluj.ds.websocket_service.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ro.utcluj.ds.websocket_service.dto.OverconsumptionNotification;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(OverconsumptionNotification notification) {
        messagingTemplate.convertAndSend("/topic/socket/overconsumption", notification);
        
        System.out.println("✅ Notificare trimisă prin WebSocket: " + notification.getMessage());
    }
}