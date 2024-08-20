package com.project.shelf.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
public class FileController {
    @Value("${image.upload-dir}")
    private String uploadDir;

    @Value("${image.access-path}")
    private String imgAccessPath;


    @PostMapping("/img/upload")       // 接收从客户端传入的文件 file, 并将 file 转到上传目录中, 之后向客户端发送成功响应
    public Map imgUpload(@RequestParam("img") MultipartFile file, HttpServletRequest req) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String uploadTime = sdf.format(new Date());
        File dest = new File(uploadDir + "/" + uploadTime + "-" + file.getOriginalFilename());
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        file.transferTo(dest);

        Map result = new LinkedHashMap();
        result.put("errno", 0);
        result.put("data", new String[]{"http://" + req.getHeader("host") + imgAccessPath + uploadTime + "-" + file.getOriginalFilename()});

        return result;
    }


}
