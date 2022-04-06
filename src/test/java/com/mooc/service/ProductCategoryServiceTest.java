package com.mooc.service;

import com.mooc.BaseTest;
import com.mooc.entity.ProductCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProductCategoryServiceTest extends BaseTest {
    @Autowired
    private ProductCategoryService productCategoryService;

    @Test
    public void testGetShopCategoryList(){
        List<ProductCategory> productCategoryList = productCategoryService.getProductCategoryList(1L);
        System.out.println("类别总数："+productCategoryList.size());
    }
}
