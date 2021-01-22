package com.paoperez.imageservice;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "Image")
class Image {
  @Id
  private String id;

  private String name;

  private String url;

  private String alt;

  private Integer width;

  private Integer height;
}
