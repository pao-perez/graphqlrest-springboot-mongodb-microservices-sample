package com.paoperez.avatarservice;

import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "Avatar")
class Avatar {
  @Id private String id;

  @NotBlank(message = "userName must not be blank")
  private String userName;

  @NotBlank(message = "imageId must not be blank")
  private String imageId;
}
