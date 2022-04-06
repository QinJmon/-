package com.mooc.dao;

import com.mooc.entity.Shop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopDao {

    /*分页查询店铺，可输入的条件有：店铺名（模糊），店铺状态，店铺类别，区域Id,owner
    参数有多个要用@Param
    shopCondition查询条件
    *rowIndex从第几行开始取
    * pageSize返回的条数 */
    List<Shop> queryShopList(@Param("shopCondition")Shop shopCondition,@Param("rowIndex")int rowIndex,
                             @Param("pageSize")int pageSize);

    /*返回queryShopList总数*/
    int queryShopCount(@Param("shopCondition")Shop shopCondition);


    /*通过shopid查询店铺*/
    Shop queryByShopId(long shopId);

    /*插入商铺*/
    int saveShop(Shop shop);

    /*商铺更新*/
    int updateShop(Shop shop);
}
