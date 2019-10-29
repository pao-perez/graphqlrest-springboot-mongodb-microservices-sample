package com.paoperez.avatarservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AvatarConfig {
    @Bean
    AvatarService avatarService(final AvatarRepository avatarRepository) {
        return new AvatarServiceImpl(avatarRepository);
    }
}
