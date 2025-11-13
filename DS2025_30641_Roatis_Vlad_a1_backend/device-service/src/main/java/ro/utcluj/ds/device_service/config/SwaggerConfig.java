package ro.utcluj.ds.device_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Device Service API")
                        .version("1.0")
                        .description("Documentatia Swagger pentru Device Microservice. "
                                + "Include endpointuri pentru adaugare, actualizare, stergere și afisare dispozitive."));
    }
}
