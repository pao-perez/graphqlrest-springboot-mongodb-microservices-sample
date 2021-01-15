package com.paoperez.contentservice;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Content")
public class Content {
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
