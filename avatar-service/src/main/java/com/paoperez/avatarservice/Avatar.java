package com.paoperez.avatarservice;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "Avatar")
class Avatar {
  @Id
  private String id;

  private String userName;

  private String imageId;
}
