package com.paoperez.graphqlservice.content;

import java.util.Collection;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ContentService {
  private static final String CONTENT_URL = "http://content-service/contents"; //TODO: Move to prop file
  private final WebClient.Builder webClientBuilder;

  public ContentService(final WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

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
