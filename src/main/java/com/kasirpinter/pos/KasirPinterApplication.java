package com.kasirpinter.pos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication(scanBasePackages = "com.kasirpinter.pos")
@EntityScan(basePackages = "com.kasirpinter.pos.entity")
@EnableJpaRepositories(basePackages = "com.kasirpinter.pos.repository")
public class KasirPinterApplication {

    @Value("${app.base.url}")
    private String baseUrl;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    public static void main(String[] args) {
        SpringApplication.run(KasirPinterApplication.class, args);
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//
//        corsConfiguration.setAllowedOrigins(Arrays.asList(
//                "http://localhost:3000",
//                "https://console.kasirpinter.id",
//                "https://secret-api.kasirpinter.id"
//        ));
//
//        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
//        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
//        corsConfiguration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfiguration);
//
//        return source;
//    }

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted() {
        System.out.println("Test value baseUrl: " + baseUrl);
        System.out.println("Test value database url: " + dbUrl);
    }

}