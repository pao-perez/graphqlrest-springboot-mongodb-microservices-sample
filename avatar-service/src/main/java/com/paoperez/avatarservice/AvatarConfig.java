package com.paoperez.avatarservice;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AvatarConfig {
  @Bean
  AvatarService avatarService(final AvatarRepository avatarRepository) {
    return new AvatarServiceImpl(avatarRepository);
  }

  @Bean
  ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PRIVATE);

    return mapper;
  }

  @Bean
  AvatarMapper avatarMapper(final ModelMapper modelMapper) {
    return new AvatarMapperImpl(modelMapper);
  }
}
