package com.paoperez.graphqlservice.avatar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AvatarService {
  @Value("${api.avatar.url}")
  private String avatarUrl;
  private final RestTemplate restTemplate;

  public AvatarService(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Avatar getAvatar(String id) {
    return this.restTemplate.getForObject(String.format("%s/%s", avatarUrl, id), Avatar.class);
  }
}
