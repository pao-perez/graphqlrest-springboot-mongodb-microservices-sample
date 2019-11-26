package com.paoperez.avatarservice;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;

@Data
@Builder
class Avatar {
  @Id
  private String id;
  @NotBlank(message = "userName must not be blank")
  private String userName;
  @NotBlank(message = "imageId must not be blank")
  private String imageId;
}
