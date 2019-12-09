package com.paoperez.graphqlservice.image;

import lombok.Data;

@Data
public class Image {
  private String id;
  private String name;
  private String url;
  private String alt;
  private String width;
  private String height;
}
