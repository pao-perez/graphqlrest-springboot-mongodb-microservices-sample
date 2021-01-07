package com.paoperez.contentservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ContentConfig {
  @Bean
  ContentService contentService(final ContentRepository repository) {
    return new ContentServiceImpl(repository);
  }
}
