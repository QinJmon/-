package com.mooc.dao;

import com.mooc.entity.ProductImg;

import java.util.List;

/*用于管理商品图片的dao*/
public interface ProductImgDao {

    /*批量添加商品详情图片*/
    int batchInsertProductImg(List<ProductImg> productImgList);

    /*删除指定商品下的所有详情图*/
    int deleteProductImgByProductId(long productId);

    /*根据商品id查询商品对应的详情图*/
    List<ProductImg> queryProductImgList(long productId);
}
