package com.project.shelf.service;

import com.project.shelf.entity.Doc;
import com.project.shelf.entity.DocContent;
import com.project.shelf.entity.DocExample;
import com.project.shelf.mapper.DocContentMapper;
import com.project.shelf.req.DocSaveReq;
import com.project.shelf.resp.DocContentQueryResp;
import com.project.shelf.resp.DocQueryResp;
import com.project.shelf.util.CopyUtil;
import com.project.shelf.util.RedisUtil;
import com.project.shelf.util.RequestContext;
import com.project.shelf.util.SnowFlake;
import com.project.shelf.exception.BusinessException;
import com.project.shelf.exception.BusinessExceptionCode;
import com.project.shelf.mapper.DocMapper;
import com.project.shelf.mapper.MyDocMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DocService {
    @Resource
    private DocMapper docMapper;

    @Resource
    private MyDocMapper myDocMapper;

    @Resource
    private DocContentMapper docContentMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WsService wsService;


//    public PageResp<DocQueryResp> list(DocQueryReq req) {
//        PageHelper.startPage(req.getCurrent(), req.getPageSize());
//
//        DocExample docExample = new DocExample();
//        DocExample.Criteria criteria = docExample.createCriteria();     // Criteria 类似 Where 条件
//        List<Doc> docList =  docMapper.selectByExample(docExample);
//
//        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
//        List<DocQueryResp> list =  CopyUtil.copyList(docList, DocQueryResp.class);
//
//        PageResp<DocQueryResp> pageResp = new PageResp<>();
//        pageResp.setTotal(pageInfo.getTotal());
//        pageResp.setList(list);
//        return pageResp;
//    }

//    public List<DocQueryResp> all() {
//        DocExample docExample = new DocExample();
//        docExample.setOrderByClause("sort asc");
//        List<Doc> docList =  docMapper.selectByExample(docExample);
//
//        List<DocQueryResp> list =  CopyUtil.copyList(docList, DocQueryResp.class);
//
//        return list;
//    }

    public List<DocQueryResp> list(Long ebookId) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");

        List<Doc> docList =  docMapper.selectByExample(docExample);
        List<DocQueryResp> list =  CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    @Transactional
    public void insert(DocSaveReq req) {
        Long id = snowFlake.nextId();
        Doc doc = CopyUtil.copy(req, Doc.class);
        doc.setId(id);
        doc.setViewCount(0);
        doc.setVoteCount(0);
        DocContent docContent = CopyUtil.copy(req, DocContent.class);
        docContent.setId(doc.getId());
        docMapper.insert(doc);
        docContentMapper.insert(docContent);
    }

    @Transactional
    public void update(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        DocContent docContent = CopyUtil.copy(req, DocContent.class);
        docMapper.updateByPrimaryKey(doc);
        int count = docContentMapper.updateByPrimaryKeyWithBLOBs(docContent);      // updateByPrimaryKeyWithBLOBs 用于更新大字段数据, updateByPrimaryKey 无法更新大字段数据
        if (count == 0) {         // 文档 doc 之前没有设置过内容
            docContentMapper.insert(docContent);
        }
    }

    public void delete(List<Long> idList) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(idList);
        docMapper.deleteByExample(docExample);

//        DocContentExample docContentExample = new DocContentExample();
//        DocContentExample.Criteria criteria1 = docContentExample.createCriteria();
//        criteria1.andIdIn(idList);
//        docContentMapper.deleteByExample(docContentExample);
    }

    public DocContentQueryResp getContent(Long id) {
        DocContent content = docContentMapper.selectByPrimaryKey(id);
        myDocMapper.incrementViewCount(id);
        DocContentQueryResp resp = CopyUtil.copy(content, DocContentQueryResp.class);
        return resp;
    }

    public void incrementVote(Long id) {
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600 * 24)) {
            myDocMapper.incrementVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

        Doc doc = docMapper.selectByPrimaryKey(id);
        String logID = MDC.get("LOG_ID");
        // 异步调用, 实现点赞与通知功能解耦
        wsService.sendInfo("[" + doc.getName() + "]" + " is liked!", logID);
    }
}
