package com.paoperez.imageservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ImageConfig {
  @Bean
  ImageService imageService(final ImageRepository imageRepository) {
    return new ImageServiceImpl(imageRepository);
  }
}
