package com.paoperez.graphql.services;

import com.paoperez.graphql.models.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class CategoryService {
    private static final String CATEGORY_URL = "http://localhost:8084/categories";

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<Category> getCategory(String id) {
        return this.webClientBuilder.baseUrl(CATEGORY_URL).build().get().uri("/{id}", id).retrieve()
                .bodyToMono(Category.class);
    }

}
