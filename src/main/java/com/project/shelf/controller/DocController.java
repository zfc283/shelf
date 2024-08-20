package com.project.shelf.controller;

import com.project.shelf.req.DocSaveReq;
import com.project.shelf.resp.DocContentQueryResp;
import com.project.shelf.resp.DocQueryResp;
import com.project.shelf.resp.CommonResp;
import com.project.shelf.service.DocService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DocController {
    @Resource
    private DocService docService;

//    @GetMapping("/doc/list")
//    public CommonResp list(@Valid DocQueryReq req) {        // 使用Java Bean接收请求参数. DocReq类需要对每个成员定义 getter 和 setter 才能成功接收
//        CommonResp<PageResp<DocQueryResp>> commonResp = new CommonResp<>();
//        PageResp<DocQueryResp> list = docService.list(req);
//        commonResp.setContent(list);
//        return commonResp;
//    }

//    @GetMapping("/doc/list")
//    public CommonResp all() {
//        CommonResp<List<DocQueryResp>> commonResp = new CommonResp<>();
//        List<DocQueryResp> list = docService.all();
//        commonResp.setContent(list);
//        return commonResp;
//    }

    @GetMapping("/doc/list")
    public CommonResp list(@RequestParam Long ebookId) {
        CommonResp<List<DocQueryResp>> commonResp = new CommonResp<>();
        List<DocQueryResp> list = docService.list(ebookId);
        commonResp.setContent(list);
        return commonResp;
    }

    @PostMapping("/doc")
    public CommonResp insert(@Valid @RequestBody DocSaveReq req) {
        CommonResp commonResp = new CommonResp<>();
        docService.insert(req);
        return commonResp;
    }

    @PutMapping("/doc")
    public CommonResp update(@Valid @RequestBody DocSaveReq req) {
        CommonResp commonResp = new CommonResp<>();
        docService.update(req);
        return commonResp;
    }

    @DeleteMapping("/doc/{ids}")
    public CommonResp delete(@PathVariable String ids) {
        List<String> listOfIdStr = Arrays.asList(ids.split(","));
        List<Long> listOfId = listOfIdStr.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        CommonResp commonResp = new CommonResp<>();
        docService.delete(listOfId);
        return commonResp;
    }

    @GetMapping("/doc/content")
    public CommonResp getContent(Long id) {
        CommonResp<DocContentQueryResp> commonResp = new CommonResp<>();
        DocContentQueryResp content = docService.getContent(id);
        commonResp.setContent(content);
        return commonResp;
    }

    @PutMapping("/doc/vote")
    public CommonResp incrementVote(Long id) {
        CommonResp commonResp = new CommonResp();
        docService.incrementVote(id);
        return commonResp;
    }
}
