package ro.utcluj.ds.monitoring_service.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.ds.monitoring_service.dto.HourlyConsumptionDTO;
import ro.utcluj.ds.monitoring_service.service.ConsumptionService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consumption")
@CrossOrigin
public class ConsumptionController {

    private final ConsumptionService service;

    public ConsumptionController(ConsumptionService service) {
        this.service = service;
    }

    @GetMapping("/daily")
    public List<HourlyConsumptionDTO> getDaily(
            @RequestParam Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return service.getDailyConsumption(deviceId, date);
    }
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<HourlyConsumptionDTO>> getHourlyConsumption(
        @PathVariable Long deviceId,
        @RequestParam String date) {

    return ResponseEntity.ok(
            service.getDailyConsumption(deviceId, LocalDate.parse(date))
    );
}
}
