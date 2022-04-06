package com.mooc.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mooc.cache.JedisUtil;
import com.mooc.dao.AreaDao;
import com.mooc.entity.Area;
import com.mooc.exceptions.AreaOperationException;
import com.mooc.service.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AreaServiceImpl implements AreaService {

    @Autowired
    private AreaDao areaDao;

    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;



    private static Logger logger= LoggerFactory.getLogger(AreaServiceImpl.class);

    @Transactional
    public List<Area> getListArea(){
        String key=AREALISTKEY;
        List<Area> areaList=null;
        //使用json将数据转为字符串在存到键值对里
        ObjectMapper mapper=new ObjectMapper();
        if(!jedisKeys.exists(key)){
            //去后台取数据
            areaList=areaDao.findAll();
            String jsonString;
            try {
                 jsonString = mapper.writeValueAsString(areaList);
            } catch (Exception e) {
                e.printStackTrace();
                //将异常记录到日志
                logger.error(e.getMessage());
                //因为要使用事务，所以也要抛出异常
                throw new AreaOperationException(e.getMessage());
            }
            jedisStrings.set(key,jsonString);
        }else {
            //访问的时候已经存在key
            String jsonString = jedisStrings.get(key);
            //获取到后进行转换
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Area.class);
            //将String类型的值转为相应的java对象
            try {
                areaList=mapper.readValue(jsonString,javaType);
            } catch (Exception e) {
                e.printStackTrace();
                //将异常记录到日志
                logger.error(e.getMessage());
                //因为要使用事务，所以也要抛出异常
                throw new AreaOperationException(e.getMessage());
            }
        }
        return areaList;
    }
}
