package com.lei.dao;

import com.lei.model.Fileinfo;
import com.lei.model.FileinfoExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileinfoMapper {
    int countByExample(FileinfoExample example);

    int deleteByExample(FileinfoExample example);

    int insert(Fileinfo record);

    int insertSelective(Fileinfo record);

    List<Fileinfo> selectByExample(FileinfoExample example);

    int updateByExampleSelective(@Param("record") Fileinfo record, @Param("example") FileinfoExample example);

    int updateByExample(@Param("record") Fileinfo record, @Param("example") FileinfoExample example);
}