package com.mooc.service;

import com.mooc.dto.ShopExecution;
import com.mooc.entity.Shop;
import com.mooc.exceptions.ShopOperationException;
import com.mooc.util.ImageHolder;

import java.io.File;
import java.io.InputStream;

public interface ShopService {
    /*根据shopCondition返回分页列表
    ShopExecution里面有shopList还有商铺总数，所以返回他*/
    ShopExecution getShopList(Shop shopCondition,int pageIndex,int pageSize);

    /*通过店铺id获取店铺信息*/
    Shop getByShopId(long shopId);

    /*更新店铺信息，包括图片的处理，保证事务的一致性，出错回滚数据*/
    ShopExecution modifyShop(Shop shop,ImageHolder thumbnail) throws ShopOperationException;

    int saveShop(Shop shop);

    /*InputStream 无法获得文件名，所以要再传入文件名*/
    /*注册店铺信息，包括图片处理*/
    ShopExecution addShop(Shop shop, ImageHolder thumbnail)throws ShopOperationException;
}
