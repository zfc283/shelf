package com.project.shelf.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EbookSaveReq {
    private Long id;

    @NotNull(message = "Name can not be null")
    private String name;

    private Long category1Id;

    private Long category2Id;

    private String description;

    private String cover;

    private Integer docCount;

    private Integer viewCount;

    private Integer voteCount;
}