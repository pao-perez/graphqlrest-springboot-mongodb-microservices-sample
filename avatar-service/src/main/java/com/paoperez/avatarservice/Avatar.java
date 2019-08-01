package com.paoperez.avatarservice;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Avatar {
    @Id
    private String id;
    private String userName;
    private String imageId;
}
