package com.imooc.dao;

import com.imooc.common.ServerResponse;
import com.imooc.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    int checkUsername(String username);
    User selecLogin(@Param("username") String username,@Param("password") String password);
    int checkEmail(@Param("email") String email);
    String selectQuestionByUsername(@Param("username") String username);
    int checkAnswer(@Param("username") String username,@Param("question") String question,@Param("answer")String answer);
    int updatePasswordByUsername(@Param("usernaem") String username,@Param("password") String md5Password);
    int checkPassword(@Param("password")String password,@Param("userId") Integer userId);
    int checkEmailByUserId(@Param("email") String email,@Param("userId") Integer userId);

}