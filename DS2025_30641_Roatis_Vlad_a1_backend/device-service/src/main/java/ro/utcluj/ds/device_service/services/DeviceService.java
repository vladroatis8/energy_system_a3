package ro.utcluj.ds.device_service.services;

import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ro.utcluj.ds.device_service.entities.DeviceEntity;
import ro.utcluj.ds.device_service.messaging.DeviceEventPublisher;
import ro.utcluj.ds.device_service.messaging.DeviceCreatedEvent;
import ro.utcluj.ds.device_service.messaging.DeviceDeletedEvent;
import ro.utcluj.ds.device_service.repositories.DeviceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    public DeviceService(DeviceRepository deviceRepository,
                         DeviceEventPublisher eventPublisher,
                         RestTemplate restTemplate) {
        this.deviceRepository = deviceRepository;
        this.eventPublisher = eventPublisher;
        this.restTemplate = restTemplate;
    }

    public List<DeviceEntity> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Optional<DeviceEntity> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }

    public List<DeviceEntity> getDevicesByUserId(Long userId) {
        return deviceRepository.findByUserId(userId);
    }

    public DeviceEntity createDevice(DeviceEntity device) {

        DeviceEntity saved = deviceRepository.save(device);

        eventPublisher.publishDeviceCreated(
                new DeviceCreatedEvent(
                        saved.getId(),
                        saved.getName(),
                        saved.getMaxConsumption()
                )
        );

        return saved;
    }

    public DeviceEntity updateDevice(Long id, DeviceEntity newDeviceDetails) {
        Optional<DeviceEntity> deviceData = deviceRepository.findById(id);

        if (deviceData.isPresent()) {

            DeviceEntity existingDevice = deviceData.get();
            existingDevice.setName(newDeviceDetails.getName());
            existingDevice.setDescription(newDeviceDetails.getDescription());
            existingDevice.setMaxConsumption(newDeviceDetails.getMaxConsumption());
            existingDevice.setUserId(newDeviceDetails.getUserId());
            return deviceRepository.save(existingDevice);
        } else {

            return null;
        }
    }

    public boolean deleteDevice(Long id) {
        if (deviceRepository.existsById(id)) {

            deviceRepository.deleteById(id);

            eventPublisher.publishDeviceDeleted(
                    new DeviceDeletedEvent(id)
            );

            return true;
        }
        return false;
    }

    public DeviceEntity assignDeviceToUser(Long deviceId, Long userId) {

        Optional<DeviceEntity> deviceOpt = deviceRepository.findById(deviceId);
        if (deviceOpt.isEmpty()) {
            throw new RuntimeException("Device not found with ID: " + deviceId);
        }

        String userServiceUrl = "http://user-service:8080/users/" + userId;

        try {
            System.out.println("Incerc sa contactez: " + userServiceUrl);
            ResponseEntity<String> response = restTemplate.getForEntity(userServiceUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                DeviceEntity device = deviceOpt.get();
                device.setUserId(userId);
                return deviceRepository.save(device);
            }
        } catch (Exception e) {
            System.err.println("EROARE CONEXIUNE USER-SERVICE: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Nu am putut asigna: " + e.getMessage());
        }

        return null;
    }
}
