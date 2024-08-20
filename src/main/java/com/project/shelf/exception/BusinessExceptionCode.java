package com.project.shelf.exception;

public enum BusinessExceptionCode {

    USER_LOGIN_NAME_EXIST("Login name already exists"),
    LOGIN_USER_ERROR("User login name does not exist or password is incorrect"),
    VOTE_REPEAT("You have already liked this"),
    ;

    private String desc;

    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
