package com.project.shelf.req;

import lombok.Data;

@Data
public class UserQueryReq extends PageReq {
    private String loginName;
}
