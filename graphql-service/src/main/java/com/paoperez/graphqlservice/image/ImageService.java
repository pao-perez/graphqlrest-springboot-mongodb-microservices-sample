package com.paoperez.graphqlservice.image;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ImageService {
  private static final String IMAGE_URL = "http://image-service/images"; //TODO: Move to prop file
  private final WebClient.Builder webClientBuilder;

  public ImageService(final WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  @CircuitBreaker(name = "imageService") // TODO: Define fallback - , fallbackMethod = "getFallbackImage")
  public Image getImage(String id) {
    return this.webClientBuilder
        .baseUrl(IMAGE_URL)
        .build()
        .get()
        .uri("/{id}", id)
        .retrieve()
        .bodyToMono(Image.class)
        .block();
  }
}
