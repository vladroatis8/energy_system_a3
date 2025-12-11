package ro.utcluj.ds.websocket_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverconsumptionNotification {
    
    private Long deviceId;
    private String deviceName;
    private Double currentValue;
    private Double maxConsumption;
    private LocalDateTime timestamp;
    private String message;
    
    public OverconsumptionNotification(Long deviceId, String deviceName, 
                                      Double currentValue, Double maxConsumption, 
                                      LocalDateTime timestamp) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.currentValue = currentValue;
        this.maxConsumption = maxConsumption;
        this.timestamp = timestamp;
        this.message = String.format("Device '%s' exceeded max consumption! Current: %.2f kWh, Max: %.2f kWh",
                                     deviceName, currentValue, maxConsumption);
    }
}