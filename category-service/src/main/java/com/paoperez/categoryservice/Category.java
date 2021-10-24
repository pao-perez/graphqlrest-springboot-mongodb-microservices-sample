package com.paoperez.categoryservice;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "Category")
class Category {
  @Id
  private String id;

  private String name;
}
