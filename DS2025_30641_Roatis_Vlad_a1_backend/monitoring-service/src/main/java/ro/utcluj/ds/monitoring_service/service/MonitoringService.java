package ro.utcluj.ds.monitoring_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.ds.monitoring_service.dto.MeasurementMessage;
import ro.utcluj.ds.monitoring_service.model.HourlyConsumptionEntity;
import ro.utcluj.ds.monitoring_service.repo.DeviceRepository;
import ro.utcluj.ds.monitoring_service.repo.HourlyConsumptionRepository;

import java.time.LocalDateTime;

@Service
public class MonitoringService {

    private final DeviceRepository deviceRepository;
    private final HourlyConsumptionRepository hourlyConsumptionRepository;

    public MonitoringService(DeviceRepository deviceRepository,
                             HourlyConsumptionRepository hourlyConsumptionRepository) {
        this.deviceRepository = deviceRepository;
        this.hourlyConsumptionRepository = hourlyConsumptionRepository;
    }

    @Transactional
public void processMeasurement(MeasurementMessage message) {

    Long deviceId = message.getDeviceId();

    boolean exists = deviceRepository.existsById(deviceId);

    if (!exists) {
        System.out.println("Masuratoare ignorata – deviceId " + deviceId + " nu exista în Monitoring-Service.");
        return; 
    }

    LocalDateTime ts = message.getTimestamp();
    LocalDateTime hourStart = ts.withMinute(0).withSecond(0).withNano(0);

    HourlyConsumptionEntity entity = hourlyConsumptionRepository
            .findByDeviceIdAndHour(deviceId, hourStart)
            .orElseGet(() -> new HourlyConsumptionEntity(deviceId, hourStart, 0.0));

    double oldTotal = entity.getTotal() == null ? 0.0 : entity.getTotal();
    entity.setTotal(oldTotal + message.getMeasurementValue());

    hourlyConsumptionRepository.save(entity);
}

}
