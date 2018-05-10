package com.lei.dao;

import com.lei.model.Phonestate;
import com.lei.model.PhonestateExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface PhonestateMapper {
    int countByExample(PhonestateExample example);

    int deleteByExample(PhonestateExample example);

    int insert(Phonestate record);

    int insertSelective(Phonestate record);

    List<Phonestate> selectByExample(PhonestateExample example);

    int updateByExampleSelective(@Param("record") Phonestate record, @Param("example") PhonestateExample example);

    int updateByExample(@Param("record") Phonestate record, @Param("example") PhonestateExample example);
}