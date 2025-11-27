package ro.utcluj.ds.device_service.messaging;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import ro.utcluj.ds.device_service.config.RabbitConfig;


import java.util.HashMap;
import java.util.Map;

@Service
public class DeviceEventPublisher {

    private final AmqpTemplate amqpTemplate;

    public DeviceEventPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void publishDeviceCreated(DeviceCreatedEvent event) {
    Map<String, Object> msg = new HashMap<>();
    msg.put("eventType", "CREATED");
    msg.put("id", event.getId());
    msg.put("name", event.getName());
    msg.put("maxConsumption", event.getMaxConsumption());

    amqpTemplate.convertAndSend(RabbitConfig.SYNC_QUEUE, msg);
}

    public void publishDeviceDeleted(DeviceDeletedEvent event) {
    Map<String, Object> msg = new HashMap<>();
    msg.put("eventType", "DELETED");
    msg.put("id", event.getId());

    amqpTemplate.convertAndSend(RabbitConfig.SYNC_QUEUE, msg);
}
}