package com.paoperez.graphql.models;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Category {
    private String id;
    private String name;
}
