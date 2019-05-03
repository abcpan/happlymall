package com.imooc.controller.backend;

import com.imooc.common.Const;
import com.imooc.common.ResponseCode;
import com.imooc.common.ServerResponse;
import com.imooc.pojo.User;
import com.imooc.service.interfaces.IUserService;
import com.imooc.service.interfaces.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Create by alex
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired ICategoryService iCategoryService;

    //添加品类
    @RequestMapping(value = "category_add",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse  addCategory(HttpSession session,String categoryName,
                                       @RequestParam(value="parentId",defaultValue="0")int parentId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户没有登录,请登录");
        }
        //校验下是否是管理员
        if(iUserService.checkAdminRole(user).isSucess()){
                return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return iUserService.checkAdminRole(user);
        }
    }

    //更新品类名称
    @RequestMapping(value = "category_name_set",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCatetroy(HttpSession session ,Integer categoryId,String categoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"没有登录");
        }
        if(iUserService.checkAdminRole(user).isSucess()){
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else{
            return ServerResponse.createByErrorMessage("用户不是管理员,添加品类失败");
        }
    }
    @RequestMapping(value = "category_get",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getChilddrenParalleCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue="0") int catetoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"没有登录");
        }
        if(iUserService.checkAdminRole(user).isSucess()){
            //查询子节点的品类信息 ,并且没有递归
            return iCategoryService.getChildrenParallelCategory(catetoryId);
        }else{
            return ServerResponse.createByErrorMessage("用户不是管理员,无权操作商品管理");
        }
    }
    //查询当前节点的id和递归子节点
    @RequestMapping(value = "deep_category_get",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue="0") int catetoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"没有登录");
        }
        if(iUserService.checkAdminRole(user).isSucess()){
            //查询子节点的品类信息 ,并且没有递归
            return iCategoryService.selectCategoryAndChildrenById(catetoryId);  //进行递归
        }else{
            return ServerResponse.createByErrorMessage("用户不是管理员,无权操作商品管理");
        }
    }
}
