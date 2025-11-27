package ro.utcluj.ds.monitoring_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.ds.monitoring_service.model.HourlyConsumptionEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HourlyConsumptionRepository extends JpaRepository<HourlyConsumptionEntity, Long> {

    Optional<HourlyConsumptionEntity> findByDeviceIdAndHour(Long deviceId, LocalDateTime hour);

    List<HourlyConsumptionEntity> findByDeviceIdAndHourBetween(
            Long deviceId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<HourlyConsumptionEntity> findByDeviceIdAndHourBetweenOrderByHour(
            Long deviceId,
            LocalDateTime start,
            LocalDateTime end
    );
}
