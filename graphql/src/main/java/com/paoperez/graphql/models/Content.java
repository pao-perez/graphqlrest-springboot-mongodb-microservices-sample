package com.paoperez.graphql.models;

import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Content {
    private String id;
    @Nullable
    private String created;
    @Nullable
    private String updated;
    @Nullable
    private String title;
    @Nullable
    private String body;
    @Nullable
    private Integer rank;
    @Nullable
    private Image image;
    @Nullable
    private Category category;
    @Nullable
    private Avatar avatar;
    @Nullable
    private String imageId;
    @Nullable
    private String avatarId;
    @Nullable
    private String categoryId;
}
