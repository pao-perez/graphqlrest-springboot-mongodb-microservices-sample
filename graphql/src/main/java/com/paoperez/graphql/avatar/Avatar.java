package com.paoperez.graphql.avatar;

import com.paoperez.graphql.image.Image;

import lombok.Value;

@Value
public class Avatar {
    private String id;
    private String userName;
    private Image image;
    private String imageId;
}
