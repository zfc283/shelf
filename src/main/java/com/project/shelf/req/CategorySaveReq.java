package com.project.shelf.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategorySaveReq {
    private Long id;

    private Long parent;

    @NotNull(message = "Name can not be null")
    private String name;

    @NotNull(message = "Display order can not be null")
    private Integer sort;
}