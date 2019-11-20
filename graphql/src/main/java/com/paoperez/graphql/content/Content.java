package com.paoperez.graphql.content;

import com.paoperez.graphql.avatar.Avatar;
import com.paoperez.graphql.category.Category;
import com.paoperez.graphql.image.Image;

import lombok.Value;

@Value
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
