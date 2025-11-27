package ro.utcluj.ds.monitoring_service.dto;

import java.time.LocalDateTime;

public class MeasurementMessage {

    private LocalDateTime timestamp;
    private Long deviceId;
    private Double measurementValue;

    public MeasurementMessage() {}

    public MeasurementMessage(LocalDateTime timestamp, Long deviceId, Double measurementValue) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.measurementValue = measurementValue;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Double getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(Double measurementValue) {
        this.measurementValue = measurementValue;
    }
}
