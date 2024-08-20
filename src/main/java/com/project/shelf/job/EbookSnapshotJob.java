package com.project.shelf.job;

import com.project.shelf.util.SnowFlake;
import com.project.shelf.service.EbookSnapshotService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EbookSnapshotJob {
    @Resource
    private EbookSnapshotService ebookSnapshotService;

    @Resource
    private SnowFlake snowFlake;

    /*
       每小时生成一次今日电子书快照
     */
//    @Scheduled(cron = "1/10 * * * * ?")
    @Scheduled(cron = "6 10 0/1 * * ?")
    public void cron()  {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        log.info("生成今日电子书快照开始");
        long start = System.currentTimeMillis();
        ebookSnapshotService.updateEbookSnapshotInfo();
        log.info("生成今日电子书快照结束，耗时：{}毫秒", System.currentTimeMillis() - start);
    }
}
