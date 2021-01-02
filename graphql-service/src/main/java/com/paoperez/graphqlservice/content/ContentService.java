package com.paoperez.graphqlservice.content;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ContentService {
  @Value("${api.content.url}")
  private String contentUrl;
  private final RestTemplate restTemplate;

  public ContentService(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Content getContent(String id) {
    return this.restTemplate.getForObject(String.format("%s/%s", contentUrl, id), Content.class);
  }

  public Contents getContents() {
    return this.restTemplate.getForObject(contentUrl, Contents.class);
  }
}
