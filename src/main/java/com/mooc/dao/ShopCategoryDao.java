package com.mooc.dao;


import com.mooc.entity.ShopCategory;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ShopCategoryDao {
    List<ShopCategory>  queryShopCategory(@Param("shopCategoryCondition")ShopCategory shopCategory);
}
