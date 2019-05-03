package com.imooc.service.implement;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.imooc.common.Const;
import com.imooc.common.ResponseCode;
import com.imooc.common.ServerResponse;
import com.imooc.dao.CategoryMapper;
import com.imooc.dao.ProductMapper;
import com.imooc.pojo.Category;
import com.imooc.pojo.Product;
import com.imooc.service.interfaces.ICategoryService;
import com.imooc.service.interfaces.IProductService;
import com.imooc.util.DateTimeUtil;
import com.imooc.util.PropertiesUtil;
import com.imooc.vo.ProductListVo;
import com.imooc.vo.ProductVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductService implements IProductService {
        @Autowired
        private ProductMapper productMapper;
        @Autowired
         private CategoryMapper categoryMapper;
        @Autowired
        private ICategoryService iCategoryService;
        @Override
        public ServerResponse saveOrUpdateProduct(Product product){
            if(product !=null){
                if(StringUtils.isNotBlank(product.getSubImages())){
                    String [] subImageArray = product.getSubImages().split(",");
                    if(subImageArray.length>0){
                        product.setMainImage(subImageArray[0]);
                    }
                }
                if(product.getId()!=null){
                    int resultCount = productMapper.updateByPrimaryKeySelective(product);
                    if(resultCount >0){
                        return ServerResponse.createBySucessMessage("更新产品成功");
                    }else{
                        return ServerResponse.createByErrorMessage("更新产品失败");
                    }
                }else{
                    int resultCount = productMapper.insert(product);
                    if(resultCount>0){
                        return ServerResponse.createBySucessMessage("新增产品成功");
                    }
                    return ServerResponse.createByErrorMessage("新增产品失败");
                }
            }
            return ServerResponse.createByErrorMessage(" ");
        }

        //更新销售状态
    @Override
     public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
            if(productId ==null || status==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            Product product = new Product();
            product.setId(productId);
            product.setStatus(status);
            int resultRow = productMapper.updateByPrimaryKeySelective(product);
            if(resultRow>0){
                return ServerResponse.createBySucessMessage("更改 销售状态成功");
            }
            return ServerResponse.createByErrorMessage("更改销售状态失败");
     }
    @Override
     public ServerResponse<ProductVo> manageProductDetail(Integer productId){
            if(productId == null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product ==null){
                return ServerResponse.createByErrorMessage("产品已经下架或者删除");
            }

         ProductVo productDetail = assembleProductVo(product);
         return ServerResponse.createBySuccess(productDetail);
     }

     private ProductVo assembleProductVo(Product product){
          ProductVo productDetail = new ProductVo();
          productDetail.setId(product.getId());
          productDetail.setSubtitle(product.getSubtitle());
          productDetail.setPrice(product.getPrice());
          productDetail.setMainImage(product.getMainImage());
          productDetail.setSubImages(product.getSubImages());
          productDetail.setCategoryId(product.getCategoryId());
          productDetail.setDetail(product.getDetail());
          productDetail.setName(product.getName());
          productDetail.setStock(product.getStock());
          productDetail.setStatus(product.getStatus());

         /**
          * imageHost
          * parentCategoryId
          * createTime
          * upateTime
          */
         productDetail.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
         Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
         if(category == null){
             productDetail.setParentCategoryId(0);
         }else{
             productDetail.setParentCategoryId(category.getParentId());
         }
         productDetail.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
         productDetail.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
         return productDetail;
     }

    @Override
     public ServerResponse getProductList(int page,int pageSize){
            //starPage --.start
         //填充自己的sql逻辑
         //pagehelper收尾
         PageHelper.startPage(page, pageSize);
         List<Product> productList = productMapper.selectProductList();
         List<ProductListVo> productListVoList = Lists.newArrayList();
         for(Product productItem:productList){
             ProductListVo productListVo = assembleProductListVo(productItem);
             productListVoList.add(productListVo);
         }
         PageInfo pageResult = new PageInfo(productList);
         pageResult.setList(productListVoList);
         return ServerResponse.createBySuccess(pageResult);
     }

     private ProductListVo assembleProductListVo(Product product){
            ProductListVo productListVo = new ProductListVo();
            productListVo.setId(product.getId());
            productListVo.setName(product.getName());
            productListVo.setCategoryId(product.getCategoryId());
            productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
            productListVo.setMianImage(product.getMainImage());
            productListVo.setPrice(product.getPrice());
             productListVo.setSubtitle(product.getSubtitle());
             productListVo.setStatus(product.getStatus());
            return productListVo;
     }
    @Override
     public ServerResponse<PageInfo> SearchProduct(String productName,Integer productId,Integer page,Integer pageSize){
         if(StringUtils.isNotBlank(productName)){
             productName = new StringBuffer().append("%").append(productName).append("%").toString();
         }
         PageHelper.startPage(page, pageSize);
         List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
         List<ProductListVo> productListVoList = Lists.newArrayList();
         for(Product productItem:productList){
             ProductListVo productListVo = assembleProductListVo(productItem);
             productListVoList.add(productListVo);
         }
         PageInfo pageResult = new PageInfo(productList);
         pageResult.setList(productListVoList);
         return ServerResponse.createBySuccess(pageResult);
     }


     //前台product_detail

    public ServerResponse<ProductVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("产品已经下架或者删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode() ){
            return ServerResponse.createByErrorMessage("产品已经下架或者删除");
        }

        ProductVo productDetail = assembleProductVo(product);
        return ServerResponse.createBySuccess(productDetail);
    }

    //前台产品列表

    public ServerResponse<PageInfo> getProductByKeywordCategory
            (
            String keyword,
            Integer categoryId,
            int page,
            int pageSize,
            String orderBy
            )
    {
        if(StringUtils.isBlank(keyword)&& categoryId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<>();
        if(categoryId !=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
                if(category ==null && StringUtils.isBlank(keyword)){
                    PageHelper.startPage(page,pageSize);
                    List<ProductListVo> productListVoList = Lists.newArrayList();
                    PageInfo pageInfo  = new PageInfo(productListVoList);
                    return ServerResponse.createBySuccess(pageInfo);
                }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
            }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuffer().append("%").append(keyword).append("%").toString();

        }
        PageHelper.startPage(page,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product:productList){
            productListVoList.add(assembleProductListVo(product));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
