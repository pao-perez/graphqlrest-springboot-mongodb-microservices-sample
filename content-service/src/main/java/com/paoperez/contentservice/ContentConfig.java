package com.paoperez.contentservice;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ContentConfig {
  @Bean
  ContentService contentService(final ContentRepository repository) {
    return new ContentServiceImpl(repository);
  }

  @Bean
  ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PRIVATE);

    return mapper;
  }

  @Bean
  ContentMapper contentMapper(final ModelMapper modelMapper) {
    return new ContentMapperImpl(modelMapper);
  }
}
