package ro.utcluj.ds.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate; // <-- IMPORTĂ ACEASTA

@Configuration
public class SecurityConfig {

    // "Rețeta" pentru mașina de criptat
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- AICI ESTE PIESA LIPSĂ ---
    // "Rețeta" pentru "telefonul" HTTP
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}