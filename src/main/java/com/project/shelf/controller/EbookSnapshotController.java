package com.project.shelf.controller;

import com.project.shelf.resp.CommonResp;
import com.project.shelf.resp.StatisticsResp;
import com.project.shelf.service.EbookSnapshotService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ebook-snapshot")
public class EbookSnapshotController {
    @Resource
    private EbookSnapshotService ebookSnapshotService;
    @GetMapping("/statistics")
    public CommonResp<List<StatisticsResp>> getStatistics() {
        CommonResp<List<StatisticsResp>> commonResp = new CommonResp<>();
        List<StatisticsResp> list = ebookSnapshotService.getStatistics();
        commonResp.setContent(list);
        return commonResp;
    }

    @GetMapping("/30-statistics")
    public CommonResp<List<StatisticsResp>> getStatistics30() {
        CommonResp<List<StatisticsResp>> commonResp = new CommonResp<>();
        List<StatisticsResp> list = ebookSnapshotService.getStatistics30();
        commonResp.setContent(list);
        return commonResp;
    }
}
