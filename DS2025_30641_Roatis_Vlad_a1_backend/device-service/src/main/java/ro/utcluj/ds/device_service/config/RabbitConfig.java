package ro.utcluj.ds.device_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;             
@Configuration
public class RabbitConfig {

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