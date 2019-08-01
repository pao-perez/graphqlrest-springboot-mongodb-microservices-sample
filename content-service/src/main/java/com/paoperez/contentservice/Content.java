package com.paoperez.contentservice;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Content {
    @Id
    private String id;
    private String created;
    private String updated;
    private String title;
    private String body;
    private Integer rank;
    private String categoryId;
    private String avatarId;
    private String imageId;
}
