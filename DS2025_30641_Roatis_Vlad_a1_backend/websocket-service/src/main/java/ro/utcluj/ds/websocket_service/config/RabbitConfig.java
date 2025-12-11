package ro.utcluj.ds.websocket_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${app.queue.overconsumption}")
    private String overconsumptionQueue;

    @Bean
    public Queue overconsumptionQueue() {
        return new Queue(overconsumptionQueue, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        // 1. Creăm manual instanța de ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        
        // 2. Activăm modulul pentru LocalDateTime (Java 8 Date/Time)
        mapper.registerModule(new JavaTimeModule());

        // 3. Returnăm convertorul folosind mapper-ul configurat de noi
        return new Jackson2JsonMessageConverter(mapper);
    }
}