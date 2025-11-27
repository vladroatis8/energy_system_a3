package ro.utcluj.ds.monitoring_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ro.utcluj.ds.monitoring_service.model.DeviceEntity;
import ro.utcluj.ds.monitoring_service.repo.DeviceRepository;
import ro.utcluj.ds.monitoring_service.config.RabbitConfig;

@Service
public class DeviceSyncListener {

    private final DeviceRepository deviceRepository;

    public DeviceSyncListener(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @RabbitListener(queues = RabbitConfig.DEVICE_CREATED_QUEUE)
    @Transactional
    public void handleDeviceCreated(DeviceCreatedEvent event) {

        DeviceEntity device = deviceRepository.findById(event.getId())
                .orElse(new DeviceEntity());

        device.setId(event.getId());
        device.setName(event.getName());
        device.setMaxConsumption(event.getMaxConsumption());

        deviceRepository.save(device);

        System.out.println(" Device sincronizat în Monitoring-Service: " + event.getId());
    }

    @RabbitListener(queues = RabbitConfig.DEVICE_DELETED_QUEUE)
    @Transactional
    public void handleDeviceDeleted(DeviceDeletedEvent event) {

        deviceRepository.deleteById(event.getId());

        System.out.println(" Device sters din Monitoring-Service: " + event.getId());
    }
}
