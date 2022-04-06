package com.mooc.service.impl;

import com.mooc.dao.ShopDao;
import com.mooc.dto.ShopExecution;
import com.mooc.entity.Shop;
import com.mooc.enums.ShopStateEnum;
import com.mooc.exceptions.ShopOperationException;
import com.mooc.service.ShopService;
import com.mooc.util.ImageHolder;
import com.mooc.util.ImageUtil;
import com.mooc.util.PageCalculator;
import com.mooc.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopDao shopDao;

    /*因为前端只认页数，dao只认行数，所以编写工具类进行转换*/
    public ShopExecution getShopList(Shop shopCondition, int pageIndex, int pageSize) {
        //将pageIndex转为rowIndex
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        //返回需要的列表
        List<Shop> shopList = shopDao.queryShopList(shopCondition, rowIndex, pageSize);
        //获取总数
        int count = shopDao.queryShopCount(shopCondition);
        //将上面两个值赋给ShopExecution
        ShopExecution se=new ShopExecution();
        if(shopList!=null){
            se.setShopList(shopList);
            se.setCount(count);
        }else {
            se.setState(ShopStateEnum.INNER_ERROR.getState());
        }
        return se;
    }

    public Shop getByShopId(long shopId) {
        return shopDao.queryByShopId(shopId);
    }

    public ShopExecution modifyShop(Shop shop, ImageHolder thumbnail) throws ShopOperationException {
        if (shop == null|| shop.getShopId()==null) {
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }else {
            try {
                //1.判断是否要处理图片
                if (thumbnail.getImage() != null && thumbnail.getImageName() != null && !"".equals(thumbnail.getImageName())) {
                    Shop tempShop = shopDao.queryByShopId(shop.getShopId());
                    //当之前的文件不为空，需要将之前的先删除
                    if (tempShop.getShopImg() != null) {
                        ImageUtil.deleteFileOrPath(tempShop.getShopImg());
                    }
                    //再添加新的
                    addShopImg(shop, thumbnail);
                }
                //2.更新店铺信息
                shop.setLastEditTime(new Date());
                int effectedNum = shopDao.updateShop(shop);
                //判断操作是否成功
                if (effectedNum <= 0) {
                    return new ShopExecution(ShopStateEnum.INNER_ERROR);
                } else {
                    //成功的话，将其返回前台
                    shop = shopDao.queryByShopId(shop.getShopId());
                    return new ShopExecution(ShopStateEnum.Success, shop);
                }
            }catch (Exception e){
                //以上两步有一步出错就catch
                throw  new ShopOperationException("modifyShop error:"+e.getMessage());
            }
        }


    }

    public int saveShop(Shop shop) {
        return shopDao.saveShop(shop);
    }

    @Transactional
    public ShopExecution addShop(Shop shop, ImageHolder thumbnail) {
        if (shop == null) {
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }
        try{
            //给店铺信息赋初始值
            shop.setEnableStatus(0);
            shop.setCreateTime(new Date());
            shop.setLastEditTime(new Date());
            //添加店铺信息
            int effectedNum=shopDao.saveShop(shop);
            if(effectedNum<=0){
                throw new RuntimeException("店铺创建失败");
            }else {
                if (thumbnail.getImage() !=null){
                    try{
                        //存储图片
                        addShopImg(shop, thumbnail);
                    }catch (Exception e){
                       throw new RuntimeException("addShopImg error:"+e.getMessage());
                    }
                    //更新店铺的图片地址
                    effectedNum=shopDao.updateShop(shop);
                    if(effectedNum<=0){
                        throw new RuntimeException("更新图片地址失败");
                    }

                }

            }

        }catch (Exception e){
            throw new ShopOperationException("addShop error:"+e.getMessage());
        }
        return new ShopExecution(ShopStateEnum.CHECK,shop);
    }

    private void addShopImg(Shop shop,ImageHolder thumbnail) {
        //获取shop图片目录的相对值路径
        String dest= PathUtil.getShopImagePath(shop.getShopId());
        String shopImgAddr= ImageUtil.generateThumbnail(thumbnail,dest);
        shop.setShopImg(shopImgAddr);
    }
}
