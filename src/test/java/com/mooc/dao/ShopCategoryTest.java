package com.mooc.dao;

import com.mooc.BaseTest;
import com.mooc.entity.ShopCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ShopCategoryTest extends BaseTest {

    @Autowired
    private ShopCategoryDao shopCategoryDao;

    @Test
    public void testQueryShopCategory(){
        //不带参数的验证
        List<ShopCategory> shopCategories = shopCategoryDao.queryShopCategory(null);
        System.out.println(shopCategories.size());

        /*ShopCategory testCategory=new ShopCategory();
        ShopCategory parentCategory=new ShopCategory();
        parentCategory.setShopCategoryId(1L);
        testCategory.setParent(parentCategory);
        //带参数的验证
        List<ShopCategory> shopCategories1 = shopCategoryDao.queryShopCategory(testCategory);
        for (ShopCategory shopCategory : shopCategories1) {
            System.out.println(shopCategory);
        }*/
    }

}
