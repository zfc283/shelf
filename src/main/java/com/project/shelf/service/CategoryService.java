package com.project.shelf.service;

import com.project.shelf.entity.Category;
import com.project.shelf.entity.CategoryExample;
import com.project.shelf.req.CategorySaveReq;
import com.project.shelf.resp.CategoryQueryResp;
import com.project.shelf.util.CopyUtil;
import com.project.shelf.util.SnowFlake;
import com.project.shelf.mapper.CategoryMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

//    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
//        PageHelper.startPage(req.getCurrent(), req.getPageSize());
//
//        CategoryExample categoryExample = new CategoryExample();
//        CategoryExample.Criteria criteria = categoryExample.createCriteria();     // Criteria 类似 Where 条件
//        List<Category> categoryList =  categoryMapper.selectByExample(categoryExample);
//
//        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
//        List<CategoryQueryResp> list =  CopyUtil.copyList(categoryList, CategoryQueryResp.class);
//
//        PageResp<CategoryQueryResp> pageResp = new PageResp<>();
//        pageResp.setTotal(pageInfo.getTotal());
//        pageResp.setList(list);
//        return pageResp;
//    }

    public List<CategoryQueryResp> all() {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> categoryList =  categoryMapper.selectByExample(categoryExample);

        List<CategoryQueryResp> list =  CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        return list;
    }

    public void insert(CategorySaveReq req) {
        Long id = snowFlake.nextId();
        Category category = CopyUtil.copy(req, Category.class);
        category.setId(id);
        categoryMapper.insert(category);
    }

    public void update(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        categoryMapper.updateByPrimaryKey(category);
    }

    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);

        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        criteria.andParentEqualTo(id);
        categoryMapper.deleteByExample(categoryExample);
    }
}
