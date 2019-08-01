package com.paoperez.imageservice;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Image {
    @Id
    private String id;
    private String name;
    private String url;
    private String alt;
    private Integer width;
    private Integer height;
}
