package com.chris.gestionpersonal.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class HttpSecurityConfig {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private AuthenticationProvider daoAuthenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(daoAuthenticationProvider)
                .authorizeHttpRequests(authorizeRequest -> {
                    // Actuator endpoints
                    authorizeRequest.requestMatchers("/").permitAll();
                    authorizeRequest.requestMatchers("/actuator/info").permitAll();
                    authorizeRequest.requestMatchers("/actuator/metrics").permitAll();
                    authorizeRequest.requestMatchers("/actuator/health").permitAll();
                    // Swagger endpoints
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/swagger-ui/**").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/v3/api-docs/**").permitAll();
                    // API endpoints
                    authorizeRequest.requestMatchers(HttpMethod.POST,"/api/v1/register").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.POST,"/api/v1/login").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.PUT,"/api/v1/employee/{id}").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/profile").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/employees").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.POST,"/api/v1/logout").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.POST,"/api/v1/sendMessage").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.POST,"/api/v1/sendMessageFile").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.POST,"/api/v1/assist").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.POST,"/api/v1/authGoogle").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/process-assists").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/assist-details").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/assist-details/excel").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/charts").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/charts/attendance-stats").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/charts/worked-hours").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/available-vacations").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.POST,"/api/v1/chat").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.POST,"/api/v1/chat/audio").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.POST,"/api/v1/calendar-events").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.PUT,"/api/v1/calendar-events/{id}").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.DELETE,"/api/v1/calendar-events/{id}").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/calendar-events/{id}").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/calendar-events").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/calendar-events/employee/{employeeId}").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/calendar-events/date-range").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/calendar-events/employee/{employeeId}/date-range").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.POST,"/api/v1/employee/{id}/upload-photo").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.DELETE,"/api/v1/employee/{id}/delete-photo").permitAll();
                    authorizeRequest.requestMatchers(HttpMethod.GET,"/api/v1/employee/{id}/debug-photo").permitAll();
                    authorizeRequest.anyRequest().authenticated();
                }).build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://127.0.0.1:5500",
            "http://127.0.0.1:5173",
            "http://localhost:5173",
            "http://localhost:4200",
            "http://localhost:8081",
            "https://www.pasteleriaprimavera.social",
            "https://gestion-personal-env.eba-mis33s3b.us-east-1.elasticbeanstalk.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // Crítico para cookies
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie")); // Exponer header Set-Cookie
        configuration.setMaxAge(3600L); // Cache preflight por 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
