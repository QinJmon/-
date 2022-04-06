package com.mooc.dao;

import com.mooc.BaseTest;
import com.mooc.entity.Product;
import com.mooc.entity.ProductCategory;
import com.mooc.entity.ProductImg;
import com.mooc.entity.Shop;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductDaoTest extends BaseTest {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private  ProductImgDao productImgDao;

    @Test
    public void testAinsertProduct(){
        Shop shop1 = new Shop();
        shop1.setShopId(1L);

        ProductCategory pc1 = new ProductCategory();
        pc1.setProductCategoryId(1L);


        //初始化三个商品实例并添加进shopId为1的店铺里
        Product product = new Product();
        product.setProductName("测试1");
        product.setProductDesc("测试desc1");
        product.setImgAddr("test1");
        product.setPriority(1);
        product.setEnableStatus(1);
        product.setCreateTime(new Date());
        product.setLastEditTime(new Date());
        product.setShop(shop1);
        product.setProductCategory(pc1);


        Product product2 = new Product();
        product2.setProductName("测试2");
        product2.setProductDesc("测试desc2");
        product2.setImgAddr("test2");
        product2.setPriority(1);
        product2.setEnableStatus(1);
        product2.setCreateTime(new Date());
        product2.setLastEditTime(new Date());
        product2.setShop(shop1);
        product2.setProductCategory(pc1);

        Product product3 = new Product();
        product3.setProductName("测试3");
        product3.setProductDesc("测试desc3");
        product3.setImgAddr("test3");
        product3.setPriority(1);
        product3.setEnableStatus(1);
        product3.setCreateTime(new Date());
        product3.setLastEditTime(new Date());
        product3.setShop(shop1);
        product3.setProductCategory(pc1);

        int effectedNum = productDao.insertProduct(product);
        assertEquals(1,effectedNum);
        effectedNum = productDao.insertProduct(product2);
        assertEquals(1,effectedNum);
        effectedNum = productDao.insertProduct(product3);
        assertEquals(1,effectedNum);



    }

    @Test
    public void testCQueryProductByProductId() throws Exception{
        //初始化两个商品详情图作为productId为的商品下的详情图
        long productId = 3;
        ProductImg productImg1 = new ProductImg();
        productImg1.setImgAddr("图1");
        productImg1.setImgDesc("测试图片1");
        productImg1.setPriority(1);
        productImg1.setCreateTime(new Date());
        productImg1.setProductId(productId);

        ProductImg productImg2 = new ProductImg();
        productImg2.setImgAddr("图1");
        productImg2.setImgDesc("测试图片1");
        productImg2.setPriority(1);
        productImg2.setCreateTime(new Date());
        productImg2.setProductId(productId);

        //批量插入到商品详情列表中
        List<ProductImg> productImgList = new ArrayList<ProductImg>();
        productImgList.add(productImg1);
        productImgList.add(productImg2);
        int effectedNum = productImgDao.batchInsertProductImg(productImgList);
        assertEquals(2,effectedNum);

        //查询productId为的商品信息
        Product product = productDao.queryProductById(productId);
        assertEquals(2,product.getProductImgList().size());
    }

    @Test
    public void testDUpdateProduct() throws Exception{
        //必须和数据库中的数据对应
        Shop shop = new Shop();
        shop.setShopId(8L);
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProductCategoryId(3L);

        Product product = new Product();
        product.setProductId(12L);
        product.setProductName("小黄人和大白");
        product.setShop(shop);
        product.setProductCategory(productCategory);

        int effectedNum = productDao.updateProduct(product);
        assertEquals(1,effectedNum);
    }

    @Test
    public void testBQueriProductList() throws Exception{
        Product productCondition = new Product();
        //分页查询，预期返回三条结果
        List<Product> productList =null;
        productList = productDao.queryProductList(productCondition,0,3);
        assertEquals(3,productList.size());

        //查询商品总数
        int count = productDao.queryProductCount(productCondition);
        assertEquals(8,count);

        //使用商品名称模糊查询
        productCondition.setProductName("测试");
        productList = productDao.queryProductList(productCondition, 0, 1);
        assertEquals(1,productList.size());
        count = productDao.queryProductCount(productCondition);
        assertEquals(2,count);
    }

    @Test
    public void testUpdateProductCategpryToNull(){
        int i = productDao.updateProductCategpryToNull(1L);
        assertEquals(2,i);
    }




}
