package com.project.shelf.resp;

import lombok.Data;

@Data
public class UserQueryResp {
    private Long id;

    private String loginName;

    private String name;

    private String password;
}
