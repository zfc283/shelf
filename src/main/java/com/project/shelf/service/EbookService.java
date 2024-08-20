package com.project.shelf.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.shelf.entity.Ebook;
import com.project.shelf.entity.EbookExample;
import com.project.shelf.mapper.EbookMapper;
import com.project.shelf.mapper.MyEbookMapper;
import com.project.shelf.req.EbookQueryReq;
import com.project.shelf.req.EbookSaveReq;
import com.project.shelf.resp.EbookQueryResp;
import com.project.shelf.resp.PageResp;
import com.project.shelf.util.CopyUtil;
import com.project.shelf.util.SnowFlake;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@Slf4j
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    @Resource
    private MyEbookMapper myEbookMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> list(EbookQueryReq req) {
        PageHelper.startPage(req.getCurrent(), req.getPageSize());

        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();     // Criteria 类似 Where 条件
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        if (!ObjectUtils.isEmpty(req.getCategoryId())) {
            criteria.andCategory2IdEqualTo(req.getCategoryId());
        }
        List<Ebook> ebookList =  ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        List<EbookQueryResp> list =  CopyUtil.copyList(ebookList, EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void insert(EbookSaveReq req) {
        Long id = snowFlake.nextId();
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        ebook.setId(id);
        ebook.setDocCount(0);
        ebook.setViewCount(0);
        ebook.setVoteCount(0);
        ebookMapper.insert(ebook);
    }

    public void update(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        ebookMapper.updateByPrimaryKey(ebook);
    }

    public void delete(Long id) {
        ebookMapper.deleteByPrimaryKey(id);
    }

    public void updateEbookInfo() {
        myEbookMapper.updateEbookInfo();
    }
}
