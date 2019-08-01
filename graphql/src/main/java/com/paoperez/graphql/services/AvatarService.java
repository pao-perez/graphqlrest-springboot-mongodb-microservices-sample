package com.paoperez.graphql.services;

import com.paoperez.graphql.models.Avatar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class AvatarService {
    private static final String AVATAR_URL = "http://localhost:8083/avatars";

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<Avatar> getAvatar(String id) {
        return this.webClientBuilder.baseUrl(AVATAR_URL).build().get().uri("/{id}", id).retrieve()
                .bodyToMono(Avatar.class);
    }

}
