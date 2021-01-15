package com.paoperez.contentservice;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "Content")
class Content {
  @Id
  private String id;
  private Long created;
  private Long updated;
  private String title;
  private String body;
  private Integer rank;
  private String categoryId;
  private String avatarId;
  private String imageId;
}
