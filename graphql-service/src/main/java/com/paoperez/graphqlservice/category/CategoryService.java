package com.paoperez.graphqlservice.category;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CategoryService {
  @Value("${api.category.url}")
  private String categoryUrl;
  private final RestTemplate restTemplate;

  public CategoryService(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Category getCategory(String id) {
    return this.restTemplate.getForObject(String.format("%s/%s", categoryUrl, id), Category.class);
  }
}
