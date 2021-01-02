package com.paoperez.avatarservice;

import java.util.Collection;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class Avatars {
    @NotBlank(message = "data must not be blank")
    private Collection<Avatar> data;
}
