package com.project.shelf.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageReq {
    @NotNull(message = "Page number can not be null")
    private Integer current;
    @NotNull(message = "Page size can not be null")
    @Max(value = 1000, message = "Page size can not exceed 1000")
    private Integer pageSize;
}
