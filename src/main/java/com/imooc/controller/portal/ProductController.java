package com.imooc.controller.portal;

import com.github.pagehelper.PageInfo;
import com.imooc.common.ServerResponse;
import com.imooc.service.interfaces.IProductService;
import com.imooc.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
@RequestMapping("/product/")
public class ProductController {
   @Autowired
   private IProductService iProductService;
   @RequestMapping(value = "product_detail_get",method = RequestMethod.GET)
   @ResponseBody
   public ServerResponse<ProductVo> detail(Integer productId){
       return iProductService.getProductDetail(productId);
   }
    @RequestMapping(value = "product_list_get",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> productList(
            @RequestParam(value="keyword",required=false) String keyword,
            @RequestParam(value="categoryId",required=false)Integer categoryId,
            @RequestParam(value="page",defaultValue="1") int page,
            @RequestParam(value="pageSize",defaultValue="10")int  pageSize,
            @RequestParam(value="orderBy") String orderBy
    ){

        return iProductService.getProductByKeywordCategory(keyword,categoryId,page,pageSize,orderBy);
    }
}
