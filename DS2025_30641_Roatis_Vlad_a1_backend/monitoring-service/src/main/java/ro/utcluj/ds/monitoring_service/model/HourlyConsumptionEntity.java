package ro.utcluj.ds.monitoring_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hourly_consumption")
public class HourlyConsumptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long deviceId;

    private LocalDateTime hour;

    private Double total;

    public HourlyConsumptionEntity() {}

    public HourlyConsumptionEntity(Long deviceId, LocalDateTime hour, Double total) {
        this.deviceId = deviceId;
        this.hour = hour;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
