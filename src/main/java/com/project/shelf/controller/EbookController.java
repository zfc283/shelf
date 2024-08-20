package com.project.shelf.controller;

import com.project.shelf.req.EbookQueryReq;
import com.project.shelf.req.EbookSaveReq;
import com.project.shelf.resp.CommonResp;
import com.project.shelf.resp.EbookQueryResp;
import com.project.shelf.resp.PageResp;
import com.project.shelf.service.EbookService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EbookController {
    @Resource
    private EbookService ebookService;

    @GetMapping("/ebook/list")
    public CommonResp list(@Valid EbookQueryReq req) {        // 使用Java Bean接收请求参数. EbookReq类需要对每个成员定义 getter 和 setter 才能成功接收
        CommonResp<PageResp<EbookQueryResp>> commonResp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        commonResp.setContent(list);
        return commonResp;
    }

    @PostMapping("/ebook")
    public CommonResp insert(@Valid @RequestBody EbookSaveReq req) {
        CommonResp commonResp = new CommonResp<>();
        ebookService.insert(req);
        return commonResp;
    }

    @PutMapping("/ebook")
    public CommonResp update(@Valid @RequestBody EbookSaveReq req) {
        CommonResp commonResp = new CommonResp<>();
        ebookService.update(req);
        return commonResp;
    }

    @DeleteMapping("/ebook/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp<>();
        ebookService.delete(id);
        return commonResp;
    }
}
