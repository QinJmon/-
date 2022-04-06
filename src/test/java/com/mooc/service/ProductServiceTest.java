package com.mooc.service;

import com.mooc.BaseTest;
import com.mooc.dto.ProductExecution;
import com.mooc.entity.Product;
import com.mooc.entity.ProductCategory;
import com.mooc.entity.ProductImg;
import com.mooc.entity.Shop;
import com.mooc.enums.ProductStateEnum;
import com.mooc.exceptions.ShopOperationException;
import com.mooc.util.ImageHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProductServiceTest extends BaseTest {
    @Autowired
    private ProductService productService;

    @Test
    public void testAddProduct() throws ShopOperationException,FileNotFoundException {
        Shop shop1 = new Shop();
        shop1.setShopId(1L);

        ProductCategory pc1 = new ProductCategory();
        pc1.setProductCategoryId(3L);



        Product product = new Product();
        product.setProductName("测试2");
        product.setProductDesc("测试商品2");
        product.setPriority(1);
        product.setCreateTime(new Date());
        product.setShop(shop1);
        product.setProductCategory(pc1);

        product.setEnableStatus(ProductStateEnum.SUCCESS.getState());

        //一张缩略图，两张商品详情图
        //创建缩略图文件流
        File thumbnailFile = new File("C:/XiaoYuanShangPu/11.jpg");
        InputStream is = new FileInputStream(thumbnailFile);
        ImageHolder thumbnail = new ImageHolder(thumbnailFile.getName(), is);
        //创建两个商品详情图片文件流并将它们添加到详情图列表中
        File productImg1 = new File("C:/XiaoYuanShangPu/11.jpg");
        InputStream is1 = new FileInputStream(productImg1);
        File productImg2 = new File("C:/XiaoYuanShangPu/22.JPG");
        InputStream is2 = new FileInputStream(productImg2);

        List<ImageHolder> productImgList = new ArrayList<ImageHolder>();
        productImgList.add(new ImageHolder(productImg1.getName(), is1));
        productImgList.add(new ImageHolder(productImg2.getName(), is2));

        //添加商品并验证
        ProductExecution pe = productService.addProduct(product,thumbnail,productImgList);
        assertEquals(ProductStateEnum.SUCCESS.getState(),pe.getState());
    }

    @Test
    public void testModifyProduct() throws ShopOperationException, FileNotFoundException {
        Shop shop1 = new Shop();
        shop1.setShopId(8L);

        ProductCategory pc1 = new ProductCategory();
        pc1.setProductCategoryId(3L);

        Product product = new Product();
        product.setProductId(3L);
        product.setShop(shop1);
        product.setProductCategory(pc1);
        product.setProductName("商品4");
        product.setProductDesc("正式商品3");

        //创建缩略图文件流
        File thumbnailFile = new File("C:/XiaoYuanShangPu/66.jpg");
        InputStream is = new FileInputStream(thumbnailFile);
        ImageHolder thumbnail = new ImageHolder(thumbnailFile.getName(), is);

        //创建两个商品详情图片文件流并将它们添加到详情图列表中
        File productImg1 = new File("C:/XiaoYuanShangPu/55.png");
        InputStream is1 = new FileInputStream(productImg1);

        File productImg2 = new File("C:/XiaoYuanShangPu/66.jpg");
        InputStream is2 = new FileInputStream(productImg2);

        List<ImageHolder> productImgList = new ArrayList<ImageHolder>();
        productImgList.add(new ImageHolder(productImg1.getName(), is1));
        productImgList.add(new ImageHolder(productImg2.getName(), is2));

        //更新商品信息
        ProductExecution pe = productService.modifyProduct(product,thumbnail,productImgList);
        assertEquals(ProductStateEnum.SUCCESS.getState(),pe.getState());
    }

}

