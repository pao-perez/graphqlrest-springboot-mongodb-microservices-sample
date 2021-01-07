package com.paoperez.contentservice;

import java.util.Collection;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentsDTO {
    @NotBlank(message = "data must not be blank")
    private Collection<ContentDTO> data;
}
