package ro.utcluj.ds.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

 @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> {}) // ✅ activează configurația CORS de mai jos
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/auth/login",
                "/auth/register",
                "/auth/ping",
                "/auth/users"
            ).permitAll()
            .anyRequest().permitAll() // 👈 temporar, ca să testăm
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

    return http.build();
}
    @Bean
public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
    corsConfig.addAllowedOriginPattern("*"); // ✅ permite toate originile (inclusiv :3000)
    corsConfig.addAllowedMethod("*");        // GET, POST, PUT, DELETE etc.
    corsConfig.addAllowedHeader("*");        // toate headerele
    corsConfig.setAllowCredentials(true);    // pentru tokenuri / cookie-uri

    var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);
    return source;
}
}