package com.project.shelf.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.regex.Pattern;

@Data
public class StatisticsResp {
    @JsonFormat(pattern = "MM-dd")
    private Date date;
    private int viewCount;
    private int voteCount;
    private int viewIncrease;
    private int voteIncrease;
}
