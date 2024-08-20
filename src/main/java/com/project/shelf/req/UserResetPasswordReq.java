package com.project.shelf.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserResetPasswordReq {
    @NotNull(message = "User ID can not be null")
    private Long id;
    @NotNull(message = "Password can not be null")
    private String password;
}
