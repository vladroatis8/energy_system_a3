package ro.utcluj.ds.monitoring_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ========================
    // 1. EXISTING MEASUREMENTS QUEUE
    // ========================

    @Value("${app.queue.device-measurements}")
    private String deviceMeasurementsQueue;

    @Bean
    public Queue deviceMeasurementsQueue() {
        return new Queue(deviceMeasurementsQueue, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ========================
    // 2. NEW SYNC QUEUES + EXCHANGE
    // ========================

    public static final String DEVICE_EXCHANGE = "device.sync.exchange";

    public static final String DEVICE_CREATED_QUEUE = "device.created.queue";
    public static final String DEVICE_DELETED_QUEUE = "device.deleted.queue";

    @Bean
    public TopicExchange deviceExchange() {
        return new TopicExchange(DEVICE_EXCHANGE);
    }

    @Bean
    public Queue deviceCreatedQueue() {
        return new Queue(DEVICE_CREATED_QUEUE, true);
    }

    @Bean
    public Queue deviceDeletedQueue() {
        return new Queue(DEVICE_DELETED_QUEUE, true);
    }

    @Bean
    public Binding deviceCreatedBinding() {
        return BindingBuilder
                .bind(deviceCreatedQueue())
                .to(deviceExchange())
                .with("device.created");
    }

    @Bean
    public Binding deviceDeletedBinding() {
        return BindingBuilder
                .bind(deviceDeletedQueue())
                .to(deviceExchange())
                .with("device.deleted");
    }


}
