package com.mooc.service;

import com.mooc.BaseTest;
import com.mooc.dto.ShopExecution;
import com.mooc.entity.Area;
import com.mooc.entity.PersonInfo;
import com.mooc.entity.Shop;
import com.mooc.entity.ShopCategory;
import com.mooc.enums.ShopStateEnum;
import com.mooc.util.ImageHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class ShopServiceTest extends BaseTest {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopCategoryService shopCategoryService;

    @Test
    public void testGetShopCategoryList(){
        ShopCategory childshopCategory=new ShopCategory();
       ShopCategory parentCategory=new ShopCategory();
       parentCategory.setShopCategoryId(7L);
       childshopCategory.setParent(parentCategory);

        List<ShopCategory> shopCategoryList = shopCategoryService.getShopCategoryList(new ShopCategory());
        System.out.println(shopCategoryList.size());
    }

    @Test
    public void testGetShopList(){
        Shop shopCondition =new Shop();
        ShopCategory shopCategory=new ShopCategory();
        shopCategory.setShopCategoryId(3L);
        shopCondition.setShopCategory(shopCategory);
        ShopExecution se = shopService.getShopList(shopCondition, 2, 2);
        System.out.println("商铺列表数量："+se.getShopList().size());
        System.out.println("商铺总数："+se.getCount());
    }


    @Test
    public void testModifyShop() throws Exception{
        Shop shop=new Shop();
        shop.setShopId(8L);
        shop.setShopName("修改后2的店铺");
        //获得图片
        File shopImg=new File("C:\\Users\\大 大\\Desktop\\About learn\\ps图片\\IMG_5194.PNG");
        //获得文件输入流
        InputStream is=new FileInputStream(shopImg);
        ImageHolder imageHolder=new ImageHolder("IMG_5194.PNG",is);
        ShopExecution shopExecution = shopService.modifyShop(shop, imageHolder);
        System.out.println("新的图片地址:"+shopExecution.getShop().getShopImg());
        System.out.println("商铺名称："+shop.getShopName());
    }

    @Test
    public void testAddShop() throws Exception {
        Shop shop=new Shop();
        PersonInfo personInfo=new PersonInfo();
        personInfo.setUserId(1L);
        Area area=new Area();
        area.setAreaId(1);
        ShopCategory shopCategory=new ShopCategory();
        shopCategory.setShopCategoryId(1L);
        shop.setOwner(personInfo);
        shop.setArea(area);
        shop.setShopCategory(shopCategory);
        shop.setPhone("55555");
        shop.setCreateTime(new Date());
        shop.setEnableStatus(ShopStateEnum.CHECK.getState());
        shop.setPriority(4);
        shop.setShopName("测试店铺3");
        shop.setLastEditTime(new Date());
       File shopImg=new File("C:\\Users\\大 大\\Desktop\\About learn\\ps图片\\11.jpg");
        //将File转为InputStream
       InputStream is=new FileInputStream(shopImg);
        ImageHolder imageHolder = new ImageHolder("11.jpg",is);
        ShopExecution se = shopService.addShop(shop, imageHolder);
        //System.out.println(se);

            System.out.println(se.getState());
            System.out.println(ShopStateEnum.CHECK.getState());


    }

}
