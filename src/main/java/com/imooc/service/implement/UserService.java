package com.imooc.service.implement;

import com.imooc.common.Const;
import com.imooc.common.ServerResponse;
import com.imooc.common.TokenCache;
import com.imooc.dao.UserMapper;
import com.imooc.pojo.User;
import com.imooc.service.interfaces.IUserService;
import com.imooc.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserService implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount ==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //MD5 密码登录
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selecLogin(username,md5Password);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

        @Override
        public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSucess()){
            return validResponse;
        }
       validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSucess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySucessMessage("注册成功");
    }
    /**
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str,String type){
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已经存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("用户邮箱已经存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySucessMessage("参数校验成功");
    }
    @Override
    public ServerResponse selectQuestion(String username){
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSucess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        System.out.println("正在查找问题");
        String question = userMapper.selectQuestionByUsername(username);
        System.out.println(question);
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("用户设置的密保问题不存在");
    }
    @Override
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount >0){
            //说明问题及问题答案存在
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createBySucessMessage("问题的答案错误");
    }
    @Override
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,需要传送参数");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSucess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(token ==null){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if(org.apache.commons.lang3.StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount>0){
                return ServerResponse.createByErrorMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token参数错误");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }
    @Override
    public ServerResponse<String> resetPassword(String passwordNew,String passwordOld,User user){
        //防止横向越权,要检验一下这个用户的旧密码
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");

        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySucessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }
    @Override
    public ServerResponse<User> updateInfomation(User user){
        //username 是不能被更新的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("邮箱已经被使用");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        System.out.println(updateCount);
        if(updateCount >0){
            return ServerResponse.createBySuccess("更新信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新信息失败");
    }


    @Override
    public ServerResponse<User> getUserInfomation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    //backend
    @Override
    public ServerResponse checkAdminRole(User user){
            if(user != null && user.getRole().intValue() ==Const.Role.ROLE_ADMIN){
                return ServerResponse.createBySuccess();
            }
            return ServerResponse.createByErrorMessage("用户不是管理员");
    }

}
