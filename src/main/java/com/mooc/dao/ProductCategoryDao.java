package com.mooc.dao;

import com.mooc.entity.ProductCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductCategoryDao {

    /*根据shopId查找所有店铺中商品类别*/
    List<ProductCategory> queryProductCategory(long shopId);

    /*批量新增商品类别
    * 返回影响的行数*/
    int batchInsertProductCategory(List<ProductCategory> productCategoryList);

    /*删除指定商品类别*/
    int deleteProductCategory(@Param("productCategoryId")long productCategoryId,@Param("shopId")long shopId);

}
