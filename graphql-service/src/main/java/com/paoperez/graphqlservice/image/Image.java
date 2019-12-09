package com.paoperez.graphqlservice.image;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Image {
  private String id;
  private String name;
  private String url;
  private String alt;
  private String width;
  private String height;
}
