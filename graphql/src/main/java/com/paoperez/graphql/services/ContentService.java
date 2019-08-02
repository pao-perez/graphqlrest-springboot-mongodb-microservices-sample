package com.paoperez.graphql.services;

import java.util.Collection;

import com.paoperez.graphql.models.Content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ContentService {
    private static final String CONTENT_URL = "http://localhost:8081/contents";

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Content getContent(String id) {
        return this.webClientBuilder.baseUrl(CONTENT_URL).build().get().uri("/{id}", id).retrieve()
                .bodyToMono(Content.class).block();
    }

    public Collection<Content> getAllContents() {
        return this.webClientBuilder.baseUrl(CONTENT_URL).build().get().accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange().flatMapMany(response -> response.bodyToFlux(Content.class)).collectList().block();
    }

}