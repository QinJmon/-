package com.mooc.service;

import com.mooc.dto.ProductExecution;
import com.mooc.entity.Product;
import com.mooc.enums.ProductStateEnum;
import com.mooc.exceptions.ProductOperationException;
import com.mooc.util.ImageHolder;

import java.util.List;

public interface ProductService {

   ProductExecution getProductList(Product productCondition,int pageIndex,int pageSize);

    /*1、处理缩略图
    * 2、处理商品详情图片
    * 3、添加商品信息
    * 将缩略图的参数封装为一个类
    * 将详情图的参数封装为一个类
    * */
    ProductExecution addProduct(Product product, ImageHolder thumbnail,
                                List<ImageHolder> productImgHolderList)throws ProductOperationException;

    /*通过商品id查询唯一的商品信息*/
    Product getProductById(long productId);

    /*修改商品信息以及商品详情图处理*/
    ProductExecution modifyProduct(Product product,
                                   ImageHolder thumbnail,
                                   List<ImageHolder> productImgHolderList)throws ProductOperationException;
}
