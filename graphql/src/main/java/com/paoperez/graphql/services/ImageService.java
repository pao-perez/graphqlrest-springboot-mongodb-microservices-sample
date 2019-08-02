package com.paoperez.graphql.services;

import com.paoperez.graphql.models.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ImageService {
    private static final String IMAGE_URL = "http://localhost:8082/images";

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Image getImage(String id) {
        return this.webClientBuilder.baseUrl(IMAGE_URL).build().get().uri("/{id}", id).retrieve()
                .bodyToMono(Image.class).block();
    }

}
