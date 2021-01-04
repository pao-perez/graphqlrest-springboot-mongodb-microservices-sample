package com.paoperez.categoryservice;

import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "Category")
class Category {
  @Id
  private String id;

  @NotBlank(message = "name must not be blank")
  private String name;
}
