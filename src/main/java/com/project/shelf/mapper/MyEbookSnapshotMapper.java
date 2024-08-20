package com.project.shelf.mapper;

import com.project.shelf.resp.StatisticsResp;

import java.util.List;

public interface MyEbookSnapshotMapper {
    void updateEbookSnapshotInfo();

    List<StatisticsResp> getStatistics();

    List<StatisticsResp> getStatistics30();
}
