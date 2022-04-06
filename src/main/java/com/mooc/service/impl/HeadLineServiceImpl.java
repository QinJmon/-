package com.mooc.service.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mooc.cache.JedisUtil;
import com.mooc.dao.HeadLineDao;
import com.mooc.entity.Area;
import com.mooc.entity.HeadLine;
import com.mooc.exceptions.AreaOperationException;
import com.mooc.exceptions.HeadLineOperationException;
import com.mooc.service.HeadLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

@Service
public class HeadLineServiceImpl implements HeadLineService {

    @Autowired
    private HeadLineDao headLineDao;
    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;


    private static Logger logger= LoggerFactory.getLogger(HeadLineServiceImpl.class);

    @Transactional
    public List<HeadLine> getHeadLineList(HeadLine headLineCondition) {
        //定义redis的key前缀
        String key=HLLISTKEY;
        //定义接收对象
        List<HeadLine> headLineList=null;
        //定义jackson数据转换操作类
        ObjectMapper mapper=new ObjectMapper();
        //拼接处redis的key(因为有参数，所以要设置前缀和拼接)
        if(headLineCondition!=null && headLineCondition.getEnableStatus()!=null){
            key=key+"_" +headLineCondition.getEnableStatus();
        }
        //判断key是否存在
        if(!jedisKeys.exists(key)){
            //若不存在，则从数据库里面取出响应数据
             headLineList = headLineDao.queryHeadLine(headLineCondition);
        //将相关的实体类集合转换为string，再存入键值对中
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(headLineList);
            } catch (Exception e) {
                e.printStackTrace();
                //将异常记录到日志
                logger.error(e.getMessage());
                //因为要使用事务，所以也要抛出异常
                throw new HeadLineOperationException(e.getMessage());
            }
            jedisStrings.set(key,jsonString);
        }else {
            //访问的时候已经存在key
            String jsonString = jedisStrings.get(key);
            //获取到后进行转换
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, HeadLine.class);
            //将String类型的值转为相应的java对象
            try {
                headLineList=mapper.readValue(jsonString,javaType);
            } catch (Exception e) {
                e.printStackTrace();
                //将异常记录到日志
                logger.error(e.getMessage());
                //因为要使用事务，所以也要抛出异常
                throw new HeadLineOperationException(e.getMessage());
            }

        }
        return headLineList;
    }
}
