package com.mooc.service;

import com.mooc.dto.ProductCategoryException;
import com.mooc.entity.ProductCategory;
import com.mooc.exceptions.ProductCategoryOperationException;

import java.util.List;

public interface ProductCategoryService {

    /*查找指定店铺下的所有商品类别*/
    List<ProductCategory> getProductCategoryList(long shopId);

    /*批量添加商品类别
    * 返回的ProductCategoryException 里面包含商品类别的一些状态
    * 抛出的异常是我们自定义的  因为要用到事务管理--RuntimeException*/
    ProductCategoryException batchAddProductCategory(List<ProductCategory> productCategoryList)
        throws ProductCategoryOperationException;

    /*删除商品类别
    * 因为商品类别下面挂载的有商品，所以应该先让商品的id置为空，再删除掉商品类别*/
    ProductCategoryException deleteProductCategory(long productCategoryId,long shopId)
            throws ProductCategoryOperationException;

}
