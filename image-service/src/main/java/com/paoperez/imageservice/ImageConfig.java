package com.paoperez.imageservice;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ImageConfig {
  @Bean
  ImageService imageService(final ImageRepository imageRepository) {
    return new ImageServiceImpl(imageRepository);
  }

  @Bean
  ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PRIVATE);

    return mapper;
  }

  @Bean
  ImageMapper imageMapper(final ModelMapper modelMapper) {
    return new ImageMapperImpl(modelMapper);
  }
}
