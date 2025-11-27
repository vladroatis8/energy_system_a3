package ro.utcluj.ds.monitoring_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.ds.monitoring_service.model.DeviceEntity;


public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
}
