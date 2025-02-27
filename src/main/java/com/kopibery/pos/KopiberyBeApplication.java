package com.kopibery.pos;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(scanBasePackages = "com.kopibery.pos")
@EntityScan(basePackages = "com.kopibery.pos.entity")
@EnableJpaRepositories(basePackages = "com.kopibery.pos.repository")
public class KopiberyBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(KopiberyBeApplication.class, args);
    }

//     @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();

//        corsConfiguration.setAllowedOrigins(Arrays.asList(
//                "http://localhost:3000"
//        ));

//        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
//        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
//        corsConfiguration.setAllowCredentials(true);

//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfiguration);

//        return source;
//    }

}
