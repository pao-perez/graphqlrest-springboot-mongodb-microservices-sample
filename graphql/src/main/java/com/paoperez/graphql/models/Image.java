package com.paoperez.graphql.models;

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
public class Image {
    private String id;
    private String name;
    private String url;
    private String alt;
    private String width;
    private String height;
}
