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

    // 1. Verificăm dacă device-ul există în tabela devices (SINCRONIZAT din Device-Service)
    boolean exists = deviceRepository.existsById(deviceId);

    if (!exists) {
        System.out.println("Masuratoare ignorata – deviceId " + deviceId + " nu exista în Monitoring-Service.");
        return; // ignorăm, nu mai creăm automat device-ul!
    }

    // 2. Determinăm ora (trunchiată la început de oră)
    LocalDateTime ts = message.getTimestamp();
    LocalDateTime hourStart = ts.withMinute(0).withSecond(0).withNano(0);

    // 3. Căutăm consumul pentru device + ora respectivă
    HourlyConsumptionEntity entity = hourlyConsumptionRepository
            .findByDeviceIdAndHour(deviceId, hourStart)
            .orElseGet(() -> new HourlyConsumptionEntity(deviceId, hourStart, 0.0));

    // 4. Calculăm noul total
    double oldTotal = entity.getTotal() == null ? 0.0 : entity.getTotal();
    entity.setTotal(oldTotal + message.getMeasurementValue());

    // 5. Salvăm consumul actualizat
    hourlyConsumptionRepository.save(entity);
}

}
