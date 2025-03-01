package com.kasirpinter.pos.config;

import com.kasirpinter.pos.security.JWTTokenFactory;
import com.kasirpinter.pos.service.UserService;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.security.Key;


@Configuration
public class AppConfig {

    @Value("${app.base.url}")
    public String baseUrl;

    @Value("${app.jwtSecret}")
    private String secret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationMs;

    @Value("${app.dev}")
    private boolean isDev;

    @Autowired
    private UserService userService;

    @Bean
    public SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Bean
    public JWTTokenFactory jwtTokenFactory(Key secret) {
        return new JWTTokenFactory(secret, userService);
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public Integer jwtExpirationMs() {
        return jwtExpirationMs;
    }

}
