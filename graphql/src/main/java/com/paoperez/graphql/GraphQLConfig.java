package com.paoperez.graphql;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import graphql.GraphQL;

@Configuration
class GraphQLConfig {
    private final GraphQLProvider graphQLProvider;

    GraphQLConfig(final GraphQLProvider graphQLProvider) {
        this.graphQLProvider = graphQLProvider;
    }

    @Bean
    GraphQL graphql() {
        return graphQLProvider.graphQL();
    }

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/graphql").allowedOrigins("http://localhost:3000");
            }
        };
    }
}
