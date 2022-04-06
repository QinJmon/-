package com.mooc.dao;

import com.mooc.BaseTest;
import com.mooc.entity.ProductCategory;
import com.mooc.entity.Shop;
import com.mooc.entity.ShopCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductCategoryDaoTest extends BaseTest {

    @Autowired
    private ProductCategoryDao productCategoryDao;

    @Test
    public void testQueryProductCategory(){
       /* Shop shop=new Shop();
        shop.setShopId(1L);*/
        List<ProductCategory> productCategoryList = productCategoryDao.queryProductCategory(8L);
        System.out.println("类别数："+productCategoryList.size());
    }

    /*测试某商铺下的商品类别批量添加*/
    @Test
    public void testABatchInsertProductCategory(){
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProductCategoryName("商品类别1");
        productCategory.setPriority(1);
        productCategory.setCreateTime(new Date());
        productCategory.setShopId(1L);
        ProductCategory productCategory2 = new ProductCategory();
        productCategory2.setProductCategoryName("商品类别2");
        productCategory2.setPriority(2);
        productCategory2.setCreateTime(new Date());
        productCategory2.setShopId(1L);

        List<ProductCategory> productCategoryList = new ArrayList<ProductCategory>();
        productCategoryList.add(productCategory);
        productCategoryList.add(productCategory2);
        int effectedNum =
                productCategoryDao.batchInsertProductCategory(productCategoryList);
        System.out.println("添加影响数量："+effectedNum);

    }

    @Test
    public void testDeleteProductCategory(){

        int deleteProductCategory = productCategoryDao.deleteProductCategory(17L, 1L);
        System.out.println("删除数量："+deleteProductCategory);
    }

}
