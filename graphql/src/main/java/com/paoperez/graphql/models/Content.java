package com.paoperez.graphql.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
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
