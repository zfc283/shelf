package com.project.shelf.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//@ComponentScan({"com.project", "com.test"})      @ComponentScan 可以扫描多个包
@ComponentScan("com.project")
@SpringBootApplication
@MapperScan("com.project.shelf.mapper")
@Slf4j
@EnableScheduling
@EnableAsync
public class ShelfApplication {

    public static void main(String[] args) {
        //SpringApplication.run(ShelfApplication.class, args);
        SpringApplication app = new SpringApplication(ShelfApplication.class);
        Environment env = app.run(args).getEnvironment();
        log.info("启动成功！！");
        log.info("地址: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
    }

}
