package com.imooc.service.interfaces;

import com.imooc.common.ServerResponse;
import com.imooc.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str,String type);
    ServerResponse selectQuestion(String username);
    ServerResponse<String> checkAnswer(String username,String question,String answer);
    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);
    ServerResponse<String> resetPassword(String passwordNew,String passwordOld,User user);
    ServerResponse<User> getUserInfomation(Integer userId);
    ServerResponse<User> updateInfomation(User user);
    ServerResponse checkAdminRole(User user);
}
