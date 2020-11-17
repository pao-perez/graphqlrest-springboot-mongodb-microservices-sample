package com.paoperez.graphqlservice.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ImageService {
  @Value("${api.image.url}")
  private String imageUrl;
  private final RestTemplate restTemplate;

  public ImageService(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Image getImage(String id) {
    return this.restTemplate.getForObject(String.format("%s/%s", imageUrl, id), Image.class);
  }
}
