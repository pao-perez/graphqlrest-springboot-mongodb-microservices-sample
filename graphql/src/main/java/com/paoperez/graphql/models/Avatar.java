package com.paoperez.graphql.models;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Avatar {
    private String id;
    private String userName;
    private Image image;
    private String imageId;
}
