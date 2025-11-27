package ro.utcluj.ds.monitoring_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${app.queue.device-measurements}")
    private String deviceMeasurementsQueue;

    @Bean
    public Queue deviceMeasurementsQueue() {
        return new Queue(deviceMeasurementsQueue, true);
    }

    public static final String SYNC_QUEUE = "device.sync.queue";

    @Bean
    public Queue syncQueue() {
        return new Queue(SYNC_QUEUE, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}