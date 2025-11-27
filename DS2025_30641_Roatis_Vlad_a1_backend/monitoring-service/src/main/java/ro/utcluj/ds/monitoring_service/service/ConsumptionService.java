package ro.utcluj.ds.monitoring_service.service;

import org.springframework.stereotype.Service;
import ro.utcluj.ds.monitoring_service.dto.HourlyConsumptionDTO;
import ro.utcluj.ds.monitoring_service.model.HourlyConsumptionEntity;
import ro.utcluj.ds.monitoring_service.repo.HourlyConsumptionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConsumptionService {

    private final HourlyConsumptionRepository hourlyConsumptionRepository;

    public ConsumptionService(HourlyConsumptionRepository hourlyConsumptionRepository) {
        this.hourlyConsumptionRepository = hourlyConsumptionRepository;
    }

    public List<HourlyConsumptionDTO> getDailyConsumption(Long deviceId, LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<HourlyConsumptionEntity> entries =
                hourlyConsumptionRepository.findByDeviceIdAndHourBetween(deviceId, start, end);

        Map<Integer, Double> totals = entries.stream()
                .collect(Collectors.toMap(
                        e -> e.getHour().getHour(),
                        HourlyConsumptionEntity::getTotal
                ));

        List<HourlyConsumptionDTO> result = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            result.add(new HourlyConsumptionDTO(h, totals.getOrDefault(h, 0.0)));
        }

        return result;
    }

    public List<HourlyConsumptionEntity> getHourlyRaw(Long deviceId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return hourlyConsumptionRepository.findByDeviceIdAndHourBetween(deviceId, start, end);
    }
}
