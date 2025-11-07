package ro.utcluj.ds.device_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.ds.device_service.entities.DeviceEntity;

import ro.utcluj.ds.device_service.services.DeviceService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/devices")
@CrossOrigin(origins = "http://localhost:3000")
public class DeviceController {

    
    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public List<DeviceEntity> getAllDevices() {
        return deviceService.getAllDevices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceEntity> getDeviceById(@PathVariable Long id) {
        Optional<DeviceEntity> device = deviceService.getDeviceById(id);

        if (device.isPresent()) {
            return new ResponseEntity<>(device.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    //pt disp unui user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeviceEntity>> getDevicesByUserId(@PathVariable Long userId) {
        List<DeviceEntity> devices = deviceService.getDevicesByUserId(userId);
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createDevice(@RequestBody DeviceEntity device) {
    try {
        DeviceEntity savedDevice = deviceService.createDevice(device);
        return new ResponseEntity<>(savedDevice, HttpStatus.CREATED);
    } catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>("Eroare la crearea device-ului: " + e.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


    @PutMapping("/{id}")
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @RequestBody DeviceEntity newDeviceDetails) {
        DeviceEntity updatedDevice = deviceService.updateDevice(id, newDeviceDetails);

        if (updatedDevice != null) {
            return new ResponseEntity<>(updatedDevice, HttpStatus.OK);
        } else {
           
            return new ResponseEntity<>("Device not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDevice(@PathVariable Long id) {
        boolean wasDeleted = deviceService.deleteDevice(id);

        if (wasDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/{deviceId}/assign/{userId}")
    public ResponseEntity<?> assignDeviceToUser(@PathVariable Long deviceId, @PathVariable Long userId) {
    DeviceEntity updated = deviceService.assignDeviceToUser(deviceId, userId);
    if (updated != null)
        return new ResponseEntity<>(updated, HttpStatus.OK);
    return new ResponseEntity<>("Device not found", HttpStatus.NOT_FOUND);
}

}