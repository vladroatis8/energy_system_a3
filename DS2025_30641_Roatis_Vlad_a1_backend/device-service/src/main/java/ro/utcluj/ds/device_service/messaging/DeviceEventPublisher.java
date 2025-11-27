package ro.utcluj.ds.device_service.messaging;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;


import static ro.utcluj.ds.device_service.config.RabbitConfig.DEVICE_EXCHANGE;

@Service
public class DeviceEventPublisher {

    private final AmqpTemplate amqpTemplate;

    public DeviceEventPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void publishDeviceCreated(DeviceCreatedEvent event) {
        amqpTemplate.convertAndSend(DEVICE_EXCHANGE, "device.created", event);
    }

    public void publishDeviceDeleted(DeviceDeletedEvent event) {
        amqpTemplate.convertAndSend(DEVICE_EXCHANGE, "device.deleted", event);
    }
}
