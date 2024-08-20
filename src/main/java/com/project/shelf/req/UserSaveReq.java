package com.project.shelf.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserSaveReq {
    private Long id;

    @NotNull(message = "Login name can not be null")
    private String loginName;

    @NotNull(message = "User name can not be null")
    private String name;

    @NotNull(message = "Password can not be null")
    private String password;
}
