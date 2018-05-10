package com.lei.dao;

import com.lei.model.Message;
import com.lei.model.MessageExample;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MessageMapper {
    int countByExample(MessageExample example);

    int deleteByExample(MessageExample example);

    int deleteByPrimaryKey(String id);

    int insert(Message record);

    int insertSelective(Message record);

    List<Message> selectByExample(MessageExample example);

    Message selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Message record, @Param("example") MessageExample example);

    int updateByExample(@Param("record") Message record, @Param("example") MessageExample example);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKey(Message record);

    @Select("select *,count(*) as count from message WHERE toid = #{userId} GROUP BY formid ORDER BY " +
            "created_date desc limit #{offset}, #{limit}")
    List<Message> selectConversationList(@Param("userId") String userId, @Param("offset") int offset, @Param("limit") int limit);

    @Update("update message set has_read = 1 where conversation_id = #{conversationId}")
    void updateMessageHasReadByConversationId(@Param("conversationId") String conversationId);
}