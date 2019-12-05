package com.paoperez.graphqlservice.avatar;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AvatarService {
  private static final String AVATAR_URL = "http://localhost:8083/avatars";
  private final WebClient.Builder webClientBuilder;

  public AvatarService(final WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

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
