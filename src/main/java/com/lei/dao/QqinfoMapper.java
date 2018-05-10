package com.lei.dao;

import com.lei.model.Qqinfo;
import com.lei.model.QqinfoExample;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QqinfoMapper {
    int countByExample(QqinfoExample example);

    int deleteByExample(QqinfoExample example);

    int insert(Qqinfo record);

    int insertSelective(Qqinfo record);

    List<Qqinfo> selectByExample(QqinfoExample example);

    int updateByExampleSelective(@Param("record") Qqinfo record, @Param("example") QqinfoExample example);

    int updateByExample(@Param("record") Qqinfo record, @Param("example") QqinfoExample example);
}