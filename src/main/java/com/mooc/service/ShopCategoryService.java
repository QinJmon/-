package com.mooc.service;

import com.mooc.entity.ShopCategory;

import java.util.List;

public interface ShopCategoryService {

    public static final String SCLISTKEY="shopcategorylist";
    /*根据查询条件返回商铺类别列表*/
    List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition);
}
