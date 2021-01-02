package com.paoperez.graphqlservice.content;

import java.util.Collection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Contents {
    private Collection<Content> data;
}
