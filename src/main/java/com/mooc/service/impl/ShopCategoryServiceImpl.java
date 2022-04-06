package com.mooc.service.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mooc.cache.JedisUtil;
import com.mooc.dao.ShopCategoryDao;
import com.mooc.entity.HeadLine;
import com.mooc.entity.ShopCategory;
import com.mooc.exceptions.HeadLineOperationException;
import com.mooc.exceptions.ShopCategoryOperationException;
import com.mooc.service.ShopCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShopCategoryServiceImpl implements ShopCategoryService  {

    @Autowired
    private ShopCategoryDao shopCategoryDao;
    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;


    private static Logger logger= LoggerFactory.getLogger(ShopCategoryServiceImpl.class);

    public List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition) {
        //定义redis的key前缀
        String key=SCLISTKEY;
        //定义接收对象
        List<ShopCategory> shopCategoryList=null;
        //定义jackson数据转换操作类
        ObjectMapper mapper=new ObjectMapper();
        //拼接处redis的key
        if(shopCategoryCondition==null){
            //若查询条件为空，则列出所有首页大类
            key=key+"_allfirstlevel";

        }else if(shopCategoryCondition!=null && shopCategoryCondition.getParentId()!=null
        && shopCategoryCondition.getParent().getShopCategoryId()!=null){
            //若parentId为非空，则列出该parentId下的所有子类别
            key=key+"_parent"+shopCategoryCondition.getParent().getShopCategoryId();
        }else if(shopCategoryCondition!=null){
            //列出所有子类别，不管其属于哪个类，都列出来
            key=key+"_allsecondlevel";
        }
        //判断key是否存在
        if(!jedisKeys.exists(key)){
            //若不存在，则从数据库里面取出响应数据
             shopCategoryList = shopCategoryDao.queryShopCategory(shopCategoryCondition);
            //将相关的实体类集合转换为string，再存入键值对中
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(shopCategoryList);
            } catch (Exception e) {
                e.printStackTrace();
                //将异常记录到日志
                logger.error(e.getMessage());
                //因为要使用事务，所以也要抛出异常
                throw new ShopCategoryOperationException(e.getMessage());
            }
            jedisStrings.set(key,jsonString);
        }else {
            //访问的时候已经存在key
            String jsonString = jedisStrings.get(key);
            //获取到后进行转换
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, ShopCategory.class);
            //将String类型的值转为相应的java对象
            try {
                shopCategoryList=mapper.readValue(jsonString,javaType);
            } catch (Exception e) {
                e.printStackTrace();
                //将异常记录到日志
                logger.error(e.getMessage());
                //因为要使用事务，所以也要抛出异常
                throw new ShopCategoryOperationException(e.getMessage());
            }

        }
        return shopCategoryList;
    }
}
