package com.paoperez.graphql.models;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Content {
    private String id;
    private String created;
    private String updated;
    private String title;
    private String body;
    private Integer rank;
    private Image image;
    private Category category;
    private Avatar avatar;
    private String imageId;
    private String avatarId;
    private String categoryId;
}
