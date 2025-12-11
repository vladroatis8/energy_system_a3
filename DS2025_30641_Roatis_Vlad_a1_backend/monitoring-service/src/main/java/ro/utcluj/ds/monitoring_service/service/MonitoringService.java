package ro.utcluj.ds.monitoring_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.ds.monitoring_service.dto.MeasurementMessage;
import ro.utcluj.ds.monitoring_service.dto.OverconsumptionNotification;
import ro.utcluj.ds.monitoring_service.messaging.OverconsumptionPublisher;
import ro.utcluj.ds.monitoring_service.model.DeviceEntity;
import ro.utcluj.ds.monitoring_service.model.HourlyConsumptionEntity;
import ro.utcluj.ds.monitoring_service.repo.DeviceRepository;
import ro.utcluj.ds.monitoring_service.repo.HourlyConsumptionRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MonitoringService {

    private final DeviceRepository deviceRepository;
    private final HourlyConsumptionRepository hourlyConsumptionRepository;
    private final OverconsumptionPublisher overconsumptionPublisher;

    public MonitoringService(DeviceRepository deviceRepository,
                             HourlyConsumptionRepository hourlyConsumptionRepository,
                             OverconsumptionPublisher overconsumptionPublisher) {
        this.deviceRepository = deviceRepository;
        this.hourlyConsumptionRepository = hourlyConsumptionRepository;
        this.overconsumptionPublisher = overconsumptionPublisher;
    }

    @Transactional
    public void processMeasurement(MeasurementMessage message) {
        Long deviceId = message.getDeviceId();

        // 1. Verificăm dacă device-ul există
        Optional<DeviceEntity> deviceOpt = deviceRepository.findById(deviceId);
        
        if (deviceOpt.isEmpty()) {
            System.out.println("⚠️ Măsurătoare ignorată – deviceId " + deviceId + " nu există.");
            return;
        }

        DeviceEntity device = deviceOpt.get();

        // 2. Determinăm ora (trunchiată la început de oră)
        LocalDateTime ts = message.getTimestamp();
        LocalDateTime hourStart = ts.withMinute(0).withSecond(0).withNano(0);

        // 3. Căutăm consumul pentru device + ora respectivă
        HourlyConsumptionEntity entity = hourlyConsumptionRepository
                .findByDeviceIdAndHour(deviceId, hourStart)
                .orElseGet(() -> new HourlyConsumptionEntity(deviceId, hourStart, 0.0));

        // 4. Calculăm noul total
        double oldTotal = entity.getTotal() == null ? 0.0 : entity.getTotal();
        double newTotal = oldTotal + message.getMeasurementValue();
        entity.setTotal(newTotal);

        // 5. Salvăm consumul actualizat
        hourlyConsumptionRepository.save(entity);

        // 6. ⚡ VERIFICĂM OVERCONSUMPTION
        if (device.getMaxConsumption() != null && newTotal > device.getMaxConsumption()) {
            System.out.println("🚨 OVERCONSUMPTION detectat pentru device " + deviceId + 
                             " (" + device.getName() + "): " + newTotal + " > " + device.getMaxConsumption());

            // Creăm notificarea
            OverconsumptionNotification notification = new OverconsumptionNotification(
                    deviceId,
                    device.getName(),
                    newTotal,
                    device.getMaxConsumption(),
                    LocalDateTime.now()
            );

            // O trimitem în coadă pentru WebSocket Service
            overconsumptionPublisher.publishOverconsumption(notification);
        }
    }
}