package com.paoperez.graphqlservice.image;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ImageService {
  private static final String IMAGE_URL = "http://image-service/images";
  private final WebClient.Builder webClientBuilder;

  public ImageService(final WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

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
