package com.paoperez.graphqlservice.content;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Collection;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ContentService {
  private static final String CONTENT_URL = "http://content-service/contents";
  private final WebClient.Builder webClientBuilder;

  public ContentService(final WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  @CircuitBreaker(name = "contentService") // TODO: Define fallback - , fallbackMethod = "getFallbackContent")
  public Content getContent(String id) {
    return this.webClientBuilder
        .baseUrl(CONTENT_URL)
        .build()
        .get()
        .uri("/{id}", id)
        .retrieve()
        .bodyToMono(Content.class)
        .block();
  }

  @CircuitBreaker(name = "contentService") // TODO: Define fallback - , fallbackMethod = "getFallbackContents")
  public Collection<Content> getAllContents() {
    return this.webClientBuilder
        .baseUrl(CONTENT_URL)
        .build()
        .get()
        .accept(MediaType.APPLICATION_STREAM_JSON)
        .exchange()
        .flatMapMany(response -> response.bodyToFlux(Content.class))
        .collectList()
        .block();
  }
}
