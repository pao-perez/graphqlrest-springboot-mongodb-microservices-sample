package com.paoperez.graphqlservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("local")
@Configuration
public class LocalConfig {
    private static final Logger logger = LoggerFactory.getLogger(BaseConfig.class);

    @Bean
    WebMvcConfigurer corsConfigurer(@Value("${client.web.address}") String clientWebAddress) {
        logger.info("Allowing cross-origin requests for /graphql from origin: " + clientWebAddress);
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/graphql").allowedOrigins(clientWebAddress);
            }
        };
    }
}