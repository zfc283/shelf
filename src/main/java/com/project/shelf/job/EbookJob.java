package com.project.shelf.job;

import com.project.shelf.service.EbookService;
import com.project.shelf.util.SnowFlake;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EbookJob {
    @Resource
    private EbookService ebookService;

    @Resource
    private SnowFlake snowFlake;

    /*
       每 30 秒更新电子书信息
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron()  {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        log.info("更新电子书下的文档数据开始");
        long start = System.currentTimeMillis();
        ebookService.updateEbookInfo();
        log.info("更新电子书下的文档数据结束，耗时：{}毫秒", System.currentTimeMillis() - start);
    }
}
