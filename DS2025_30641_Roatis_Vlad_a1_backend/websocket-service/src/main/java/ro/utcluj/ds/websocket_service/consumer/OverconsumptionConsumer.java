package ro.utcluj.ds.websocket_service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ro.utcluj.ds.websocket_service.dto.OverconsumptionNotification;
import ro.utcluj.ds.websocket_service.service.NotificationService;

@Component
public class OverconsumptionConsumer {

    private final NotificationService notificationService;

    public OverconsumptionConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "${app.queue.overconsumption}")
    public void handleOverconsumptionNotification(OverconsumptionNotification notification) {
        System.out.println("🚨 Notificare overconsumption primită: " + notification);
        
        // Trimite notificarea prin WebSocket către frontend
        notificationService.sendNotification(notification);
    }
}