package com.paoperez.graphqlservice.avatar;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AvatarService {
  private static final String AVATAR_URL = "http://avatar-service/avatars";
  private final WebClient.Builder webClientBuilder;

  public AvatarService(final WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  @CircuitBreaker(name = "avatarService") // TODO: Define fallback - , fallbackMethod = "getFallbackAvatar")
  public Avatar getAvatar(String id) {
    return this.webClientBuilder
        .baseUrl(AVATAR_URL)
        .build()
        .get()
        .uri("/{id}", id)
        .retrieve()
        .bodyToMono(Avatar.class)
        .block();
  }
}
