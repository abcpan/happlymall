package com.imooc.controller.portal;

import com.imooc.common.Const;
import com.imooc.common.ResponseCode;
import com.imooc.common.ServerResponse;
import com.imooc.pojo.User;
import com.imooc.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * create by alex.pan
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST )
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        System.out.println("启动了--------------------------------------");
        if (response.isSucess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }
    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }


    @RequestMapping(value = "check_valid", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
       return iUserService.checkValid(str,type);
    }


    @RequestMapping(value = "user_info_get", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户没有登录,无法获取当前的登录信息");
    }
    @RequestMapping(value = "forget_question_get", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getForgetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "forget_question_check", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }
    @RequestMapping(value = "forget_password_reset", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    //登录状态的重置密码
    @RequestMapping(value = "password_reset", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword (HttpSession session,String passwordOld,String passwordNew){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户没有登录");
        }else{
            return iUserService.resetPassword(passwordNew,passwordOld,user);
        }
    }
    @RequestMapping(value = "user_infomation_update", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpSession session,User user){
        User current_user = (User)session.getAttribute(Const.CURRENT_USER);
        if(current_user == null){
            return ServerResponse.createByErrorMessage("用户没有登录");
        }
        user.setUsername(current_user.getUsername());
        user.setId(current_user.getId());
       ServerResponse<User> response = iUserService.updateInfomation(user);
       if(response.isSucess()){
           response.getData().setUsername(current_user.getUsername());
           session.setAttribute(Const.CURRENT_USER,response.getData());
       }
       return response;
    }
    @RequestMapping(value = "user_infomation_get", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInfomation(HttpSession session){
        User current_user =(User) session.getAttribute(Const.CURRENT_USER);
        if(current_user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录需要强制进行登录");
        }
        return iUserService.getUserInfomation(current_user.getId());
    }
}
