package com.project.shelf.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocSaveReq {
    private Long id;

    @NotNull(message = "Ebook ID can not be null")
    private Long ebookId;
    @NotNull(message = "Parent Doc ID can not be null")
    private Long parent;
    @NotNull(message = "Name can not be null")
    private String name;
    @NotNull(message = "Display order can not be null")
    private Integer sort;
    @NotNull(message = "Doc content can not be null")
    private String content;

    private Integer viewCount;

    private Integer voteCount;
}