package com.imooc.service.interfaces;

import com.github.pagehelper.PageInfo;
import com.imooc.common.ServerResponse;
import com.imooc.pojo.Product;
import com.imooc.vo.ProductVo;

public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    ServerResponse<ProductVo> manageProductDetail(Integer productId);
    ServerResponse getProductList(int pageNum,int pageSize);
    ServerResponse<PageInfo> SearchProduct(String productName, Integer productId, Integer page, Integer pageSize);
    ServerResponse<ProductVo> getProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductByKeywordCategory
            (
                    String keyword,
                    Integer categoryId,
                    int page,
                    int pageSize,
                    String orderBy
            );
}
