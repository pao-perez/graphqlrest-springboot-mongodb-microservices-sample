package com.paoperez.graphql.models;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Image {
    private String id;
    private String name;
    private String url;
    private String alt;
    private String width;
    private String height;
}
