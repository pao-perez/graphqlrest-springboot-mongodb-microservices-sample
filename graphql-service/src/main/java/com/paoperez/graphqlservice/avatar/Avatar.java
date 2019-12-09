package com.paoperez.graphqlservice.avatar;

import com.paoperez.graphqlservice.image.Image;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Avatar {
  private String id;
  private String userName;
  private Image image;
  private String imageId;
}
