package com.lei.dao;

import com.lei.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Created by John on 2017/5/19.
 */
@Mapper
public interface UserDao {
    String TABEL_NAME = "user";
    String BASE_COLUMN = "id,name,password,salt,head_url";

    @Insert({"insert into ", TABEL_NAME, "(", BASE_COLUMN, ") values(#{id},#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Select({"select ", BASE_COLUMN, " form ", TABEL_NAME, " where id = #{id}"})
    User selectById(String id);
}
