package ro.utcluj.ds.monitoring_service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ro.utcluj.ds.monitoring_service.dto.MeasurementMessage;
import ro.utcluj.ds.monitoring_service.service.MonitoringService;

@Component
public class MeasurementConsumer {

    private final MonitoringService monitoringService;

    public MeasurementConsumer(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @RabbitListener(queues = "${app.queue.device-measurements}")
    public void handleMeasurement(MeasurementMessage message) {
        monitoringService.processMeasurement(message);
    }
}
