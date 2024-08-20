package com.project.shelf.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserLoginReq {
    @NotEmpty(message = "Login name can not be empty")
    private String loginName;
    @NotEmpty(message = "Password can not be empty")
    private String password;
}
