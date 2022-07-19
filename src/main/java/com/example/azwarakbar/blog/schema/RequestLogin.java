package com.example.azwarakbar.blog.schema;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class RequestLogin {
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
