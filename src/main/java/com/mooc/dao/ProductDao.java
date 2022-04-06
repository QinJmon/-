package com.mooc.dao;

import com.mooc.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductDao {
    /*添加商品*/
    int insertProduct(Product product);

    /*通过商品id查询商品信息*/
    Product queryProductById(long productId);

    /*更新商品信息*/
    int updateProduct(Product product);

    /*删除商品类别之前，将商品类别id置为空*/
    int updateProductCategpryToNull(long productCategoryId);


    /*分页查询店铺，可输入的条件有：店铺名（模糊），店铺状态，店铺类别，区域Id,owner
    参数有多个要用@Param
    shopCondition查询条件
    *rowIndex从第几行开始取
    * pageSize返回的条数 */
    List<Product>  queryProductList(@Param("productCondition")Product productCondition,
                                    @Param("rowIndex")int rowIndex,
                                    @Param("pageSize")int pageSize);

    /*因为进行了分页查询，所以还需要查找满足条件的总数
    * （分页只显示一部分，总的让我们知道到底有多少满足条件的数据）*/
    int queryProductCount(@Param("productCondition")Product productCondition);


}
