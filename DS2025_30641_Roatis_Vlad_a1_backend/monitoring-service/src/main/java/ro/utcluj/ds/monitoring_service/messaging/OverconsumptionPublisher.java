package ro.utcluj.ds.monitoring_service.messaging;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import ro.utcluj.ds.monitoring_service.dto.OverconsumptionNotification;

@Service
public class OverconsumptionPublisher {

    private final AmqpTemplate amqpTemplate;
    private static final String QUEUE_NAME = "overconsumption_queue";

    public OverconsumptionPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void publishOverconsumption(OverconsumptionNotification notification) {
        amqpTemplate.convertAndSend(QUEUE_NAME, notification);
        System.out.println("📤 Notificare overconsumption trimisă în coadă: " + notification);
    }
}