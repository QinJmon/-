package com.mooc.dao;

import com.mooc.BaseTest;
import com.mooc.entity.Area;
import com.mooc.entity.PersonInfo;
import com.mooc.entity.Shop;
import com.mooc.entity.ShopCategory;
import com.mooc.service.ShopService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class ShopDaoTest extends BaseTest {
    @Autowired
    private ShopDao shopDao;

    @Test
    public void testQueryShopListAndCount(){
        Shop shopCondition = new Shop();

        ShopCategory childCategory = new ShopCategory();
        ShopCategory parentCategory = new ShopCategory();
        parentCategory.setShopCategoryId(7L);
        childCategory.setParent(parentCategory);
        shopCondition.setShopCategory(childCategory);

        List<Shop> shopList =
                shopDao.queryShopList(shopCondition,0,5);
        System.out.println("店铺列表大小："+shopList.size());
        int count = shopDao.queryShopCount(shopCondition);
        System.out.println("店铺总数："+count);
    }


    @Test
    public void testQueryShopList(){
        Shop shopCondition =new Shop();
        PersonInfo owner=new PersonInfo();
        ShopCategory shopCategory=new ShopCategory();
        shopCategory.setShopCategoryId(3L);
        owner.setUserId(1L);
        shopCondition.setOwner(owner);
        shopCondition.setShopCategory(shopCategory);
        int count = shopDao.queryShopCount(shopCondition);
        List<Shop> shopList = shopDao.queryShopList(shopCondition, 0, 1);
        System.out.println("商铺列表数量："+shopList.size());
       System.out.println("商铺总数："+count);

    }

    @Test
    public void testQueryByShopId(){
        long shopId=1;

        Shop shop = shopDao.queryByShopId(shopId);
        System.out.println(shop.getArea().getAreaId());
        System.out.println(shop.getArea().getAreaName());
    }
    @Test
    @Ignore
    public void saveShopTest(){
        Shop shop=new Shop();
        PersonInfo personInfo=new PersonInfo();
        personInfo.setUserId(1L);
        Area area=new Area();
        area.setAreaId(1);
        ShopCategory shopCategory=new ShopCategory();
        shopCategory.setShopCategoryId(1L);
        shop.setShopImg("。。奶茶");
        shop.setOwner(personInfo);
        shop.setArea(area);
        shop.setShopCategory(shopCategory);
        shop.setPhone("1111");
        shop.setCreateTime(new Date());
        shop.setEnableStatus(1);
        shop.setPriority(2);
        shop.setShopDesc("商家还可以");
        shop.setShopName("xxx小卖铺");
        shop.setAdvice("建议扩大");
        shop.setLastEditTime(new Date());

        int i = shopDao.saveShop(shop);
        System.out.println(i);

    }


    @Test
    @Ignore
    public void updateShopTest(){
        Shop shop=new Shop();
        shop.setShopId(1L);
        shop.setShopName("ceshi");

        shopDao.updateShop(shop);


    }
}
