package ro.utcluj.ds.device_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ro.utcluj.ds.device_service.entities.DeviceEntity;
import ro.utcluj.ds.device_service.repositories.DeviceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

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
    
        return deviceRepository.save(device);
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
            return true; 
        }
        return false; 
    }
    @Autowired
private RestTemplate restTemplate; 

public DeviceEntity assignDeviceToUser(Long deviceId, Long userId) {
    Optional<DeviceEntity> deviceOpt = deviceRepository.findById(deviceId);
    if (deviceOpt.isEmpty()) {
        throw new RuntimeException("Device not found");
    }

    String userServiceUrl = "http://user-service/users/" + userId;
    try {
        ResponseEntity<String> response = restTemplate.getForEntity(userServiceUrl, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            DeviceEntity device = deviceOpt.get();
            device.setUserId(userId);
            return deviceRepository.save(device);
        }
    } catch (Exception e) {
        throw new RuntimeException("User not found in user-service");
    }

    return null;
}

  

}