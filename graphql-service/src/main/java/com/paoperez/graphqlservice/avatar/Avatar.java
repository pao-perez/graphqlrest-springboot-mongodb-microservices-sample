package com.paoperez.graphqlservice.avatar;

import com.paoperez.graphqlservice.image.Image;

import lombok.Data;

@Data
public class Avatar {
  private String id;
  private String userName;
  private Image image;
  private String imageId;
}
