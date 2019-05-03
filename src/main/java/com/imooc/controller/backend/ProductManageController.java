package com.imooc.controller.backend;

import com.google.common.collect.Maps;
import com.imooc.common.Const;
import com.imooc.common.ResponseCode;
import com.imooc.common.ServerResponse;
import com.imooc.pojo.Product;
import com.imooc.pojo.User;
import com.imooc.service.interfaces.IFileService;
import com.imooc.service.interfaces.IProductService;
import com.imooc.service.interfaces.IUserService;
import com.imooc.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {
    @Autowired
    private IUserService  iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;
    //产品新增或者更新
    @RequestMapping(value = "product_update",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse saveProduct(HttpSession session, Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户没有登录,无权限操作");
        }
        if(iUserService.checkAdminRole(user).isSucess()){
                //填充我们增加产品的业务逻辑
                return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping(value = "sale_status_set",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session,@RequestParam(value="productId")Integer productId,@RequestParam(value="status")Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户没有登录,禁止操作");
        }
        if(iUserService.checkAdminRole(user).isSucess()){
            //填充我们增加产品的业务逻辑
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping(value = "product_detail_get",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session,@RequestParam(value="productId")Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户没有登录,禁止操作");
        }
        if(iUserService.checkAdminRole(user).isSucess()){
            //填充我们增加产品的业务逻辑
            return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping(value = "product_list_get",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getProductList(
            HttpSession session,
            @RequestParam(value="page",defaultValue = "1")Integer page,
            @RequestParam(value="pageSize",defaultValue="10") Integer pageSize
    ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户没有登录,禁止操作");
        }
        if(iUserService.checkAdminRole(user).isSucess()){
            //填充我们增加产品的业务逻辑
            return iProductService.getProductList(page,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
    @RequestMapping(value = "product_search",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getProductList(
            HttpSession session,
            String productName,
            Integer productId,
            @RequestParam(value="page",defaultValue = "1")Integer page,
            @RequestParam(value="pageSize",defaultValue="10") Integer pageSize
    ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户没有登录,禁止操作");
        }
        if(iUserService.checkAdminRole(user).isSucess()){
            //填充我们增加产品的业务逻辑
            return iProductService.SearchProduct(productName,productId,page,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
    @RequestMapping(value = "img_upload",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value="upload_img",required=false)MultipartFile file,HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户没有登录,请登录后进行文件上传");
        }
        if(iUserService.checkAdminRole(user).isSucess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }
        return ServerResponse.createByErrorMessage("您没有权限上传文件");
    }
    @RequestMapping(value = "richtext_upload",method = RequestMethod.POST)
    @ResponseBody
    public Map richTextUpload(HttpSession session, @RequestParam(value="upload_richtext",required=false)MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        Map map = new HashMap();
        if(user == null){
            map.put("success",false);
            map.put("msg","请登录管理员");
            return map;
        }
        if(iUserService.checkAdminRole(user).isSucess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(targetFileName ==null){
                map.put("success",false);
                map.put("msg","文件上传失败");
                return map;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            map.put("uri",targetFileName);
            map.put("url",url);
            map.put("success",true);
            map.put("msg","文件上传成功");
            response.addHeader("Access-Control-Allow-Header","X-File-Name");
            return map;
        }
        map.put("success",false);
        map.put("msg","您没有权限进行文件上传");
        return map;
    }
}
