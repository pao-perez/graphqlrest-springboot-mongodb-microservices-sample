package com.paoperez.contentservice;

import com.paoperez.contentservice.util.DateTimeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ContentConfig {
  @Bean
  DateTimeFactory dateTimeFactory() {
    return new DateTimeFactory();
  }

  @Bean
  Content.Builder builder(final DateTimeFactory factory) {
    return new Content.Builder(factory);
  }

  @Bean
  ContentService contentService(final ContentRepository repository, final Content.Builder builder) {
    return new ContentServiceImpl(repository, builder);
  }
}
