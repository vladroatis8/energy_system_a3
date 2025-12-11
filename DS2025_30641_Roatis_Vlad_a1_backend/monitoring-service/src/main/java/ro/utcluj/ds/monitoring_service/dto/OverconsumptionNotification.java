package ro.utcluj.ds.monitoring_service.dto;

import java.time.LocalDateTime;

public class OverconsumptionNotification {
    
    private Long deviceId;
    private String deviceName;
    private Double currentValue;
    private Double maxConsumption;
    private LocalDateTime timestamp;
    private String message;

    public OverconsumptionNotification() {}

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

    // Getters & Setters
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    
    public Double getCurrentValue() { return currentValue; }
    public void setCurrentValue(Double currentValue) { this.currentValue = currentValue; }
    
    public Double getMaxConsumption() { return maxConsumption; }
    public void setMaxConsumption(Double maxConsumption) { this.maxConsumption = maxConsumption; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}