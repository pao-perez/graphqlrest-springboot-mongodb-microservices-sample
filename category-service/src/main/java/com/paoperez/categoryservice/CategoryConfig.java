package com.paoperez.categoryservice;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CategoryConfig {
  @Bean
  CategoryService categoryService(final CategoryRepository categoryRepository) {
    return new CategoryServiceImpl(categoryRepository);
  }

  @Bean
  ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PRIVATE);

    return mapper;
  }

  @Bean
  CategoryMapper categoryMapper(final ModelMapper modelMapper) {
    return new CategoryMapperImpl(modelMapper);
  }
}
