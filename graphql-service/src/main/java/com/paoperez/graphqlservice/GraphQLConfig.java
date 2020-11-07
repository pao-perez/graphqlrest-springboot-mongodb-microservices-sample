package com.paoperez.graphqlservice;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class GraphQLConfig {
  @Value("${web.client.address}")
  private String webClientAddress;

  @Bean
  @LoadBalanced
  WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  @Bean
  GraphQL graphql(final GraphQLSchema schema) {
    return GraphQL.newGraphQL(schema).build();
  }

  @Bean
  GraphQLSchema schema(
      final SchemaGenerator generator, final SchemaParser parser, final RuntimeWiring wiring)
      throws IOException {
    URL url = Resources.getResource("schema.graphqls");
    String sdl = Resources.toString(url, Charsets.UTF_8);
    TypeDefinitionRegistry typeRegistry = parser.parse(sdl);
    return generator.makeExecutableSchema(typeRegistry, wiring);
  }

  @Bean
  RuntimeWiring runtimeWiring(final GraphQLDataFetchers dataFetchers) {
    return RuntimeWiring.newRuntimeWiring()
        .type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("contents", dataFetchers.getAllContentsDataFetcher()))
        .type(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("content", dataFetchers.getContentDataFetcher()))
        .type(
            TypeRuntimeWiring.newTypeWiring("Content")
                .dataFetcher("image", dataFetchers.getContentImageDataFetcher())
                .dataFetcher("category", dataFetchers.getCategoryDataFetcher())
                .dataFetcher("avatar", dataFetchers.getAvatarDataFetcher()))
        .type(
            TypeRuntimeWiring.newTypeWiring("Avatar")
                .dataFetcher("image", dataFetchers.getAvatarImageDataFetcher()))
        .build();
  }

  @Bean
  SchemaParser schemaParser() {
    return new SchemaParser();
  }

  @Bean
  SchemaGenerator schemaGenerator() {
    return new SchemaGenerator();
  }

  @Bean
  WebMvcConfigurer corsConfigurer() {    
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/graphql").allowedOrigins(webClientAddress);
      }
    };
  }
}
