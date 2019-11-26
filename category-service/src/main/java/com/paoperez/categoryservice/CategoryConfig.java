package com.paoperez.categoryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CategoryConfig {
  @Bean
  CategoryService categoryService(final CategoryRepository categoryRepository) {
    return new CategoryServiceImpl(categoryRepository);
  }
}
