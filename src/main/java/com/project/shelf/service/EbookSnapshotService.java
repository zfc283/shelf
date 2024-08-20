package com.project.shelf.service;


import com.project.shelf.mapper.MyEbookSnapshotMapper;
import com.project.shelf.resp.StatisticsResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EbookSnapshotService {
    @Resource
    private MyEbookSnapshotMapper myEbookSnapshotMapper;

    @Transactional
    public void updateEbookSnapshotInfo() {
        myEbookSnapshotMapper.updateEbookSnapshotInfo();
    }

    public List<StatisticsResp> getStatistics() {
        List<StatisticsResp> list = myEbookSnapshotMapper.getStatistics();
        return list;
    }

    public List<StatisticsResp> getStatistics30() {
        List<StatisticsResp> list = myEbookSnapshotMapper.getStatistics30();
        return list;
    }
}
