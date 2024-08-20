package com.project.shelf.mapper;

import com.project.shelf.entity.DocContentExample;
import com.project.shelf.entity.DocContent;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface DocContentMapper {
    long countByExample(DocContentExample example);

    int deleteByExample(DocContentExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DocContent row);

    int insertSelective(DocContent row);

    List<DocContent> selectByExampleWithBLOBs(DocContentExample example);

    List<DocContent> selectByExample(DocContentExample example);

    DocContent selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DocContent row, @Param("example") DocContentExample example);

    int updateByExampleWithBLOBs(@Param("row") DocContent row, @Param("example") DocContentExample example);

    int updateByExample(@Param("row") DocContent row, @Param("example") DocContentExample example);

    int updateByPrimaryKeySelective(DocContent row);

    int updateByPrimaryKeyWithBLOBs(DocContent row);
}