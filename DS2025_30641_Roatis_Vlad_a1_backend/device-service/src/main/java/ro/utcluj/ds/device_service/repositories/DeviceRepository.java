package ro.utcluj.ds.device_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.ds.device_service.entities.DeviceEntity;
import java.util.List;
public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
   List<DeviceEntity> findByUserId(Long userId);
}