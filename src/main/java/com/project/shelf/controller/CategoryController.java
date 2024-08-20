package com.project.shelf.controller;

import com.project.shelf.req.CategorySaveReq;
import com.project.shelf.resp.CommonResp;
import com.project.shelf.resp.CategoryQueryResp;
import com.project.shelf.service.CategoryService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

//    @GetMapping("/category/list")
//    public CommonResp list(@Valid CategoryQueryReq req) {        // 使用Java Bean接收请求参数. CategoryReq类需要对每个成员定义 getter 和 setter 才能成功接收
//        CommonResp<PageResp<CategoryQueryResp>> commonResp = new CommonResp<>();
//        PageResp<CategoryQueryResp> list = categoryService.list(req);
//        commonResp.setContent(list);
//        return commonResp;
//    }

    @GetMapping("/category/list")
    public CommonResp all() {
        CommonResp<List<CategoryQueryResp>> commonResp = new CommonResp<>();
        List<CategoryQueryResp> list = categoryService.all();
        commonResp.setContent(list);
        return commonResp;
    }

    @PostMapping("/category")
    public CommonResp insert(@Valid @RequestBody CategorySaveReq req) {
        CommonResp commonResp = new CommonResp<>();
        categoryService.insert(req);
        return commonResp;
    }

    @PutMapping("/category")
    public CommonResp update(@Valid @RequestBody CategorySaveReq req) {
        CommonResp commonResp = new CommonResp<>();
        categoryService.update(req);
        return commonResp;
    }

    @DeleteMapping("/category/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp<>();
        categoryService.delete(id);
        return commonResp;
    }
}
