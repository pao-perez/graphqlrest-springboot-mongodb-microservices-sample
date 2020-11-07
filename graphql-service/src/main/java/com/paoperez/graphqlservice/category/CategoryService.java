package com.paoperez.graphqlservice.category;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CategoryService {
  private static final String CATEGORY_URL = "http://category-service/categories";
  private final WebClient.Builder webClientBuilder;

  public CategoryService(final WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  public Category getCategory(String id) {
    return this.webClientBuilder
        .baseUrl(CATEGORY_URL)
        .build()
        .get()
        .uri("/{id}", id)
        .retrieve()
        .bodyToMono(Category.class)
        .block();
  }
}
