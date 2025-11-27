package ro.utcluj.ds.monitoring_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.ds.monitoring_service.model.DeviceEntity;
import ro.utcluj.ds.monitoring_service.repo.DeviceRepository;
import ro.utcluj.ds.monitoring_service.config.RabbitConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.Map;

@Service
public class DeviceSyncListener {

    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper;

    public DeviceSyncListener(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @RabbitListener(queues = RabbitConfig.SYNC_QUEUE)
    @Transactional
    public void handleDeviceSync(Map<String, Object> message) {
        String eventType = (String) message.get("eventType");
        
        System.out.println("Primit eveniment: " + eventType);

        if ("CREATED".equals(eventType)) {
            DeviceCreatedEvent event = objectMapper.convertValue(message, DeviceCreatedEvent.class);
            handleDeviceCreated(event);
        } else if ("DELETED".equals(eventType)) {
            DeviceDeletedEvent event = objectMapper.convertValue(message, DeviceDeletedEvent.class);
            handleDeviceDeleted(event);
        }
    }

    private void handleDeviceCreated(DeviceCreatedEvent event) {
        DeviceEntity device = deviceRepository.findById(event.getId())
                .orElse(new DeviceEntity());

        device.setId(event.getId());
        device.setName(event.getName());
        device.setMaxConsumption(event.getMaxConsumption());

        deviceRepository.save(device);
        System.out.println("Device sincronizat n Monitoring-service: " + event.getId());
    }

    private void handleDeviceDeleted(DeviceDeletedEvent event) {
        deviceRepository.deleteById(event.getId());
        System.out.println("Device sters din Monitoring-service: " + event.getId());
    }
}