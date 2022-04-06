package com.mooc.dao;

import com.mooc.BaseTest;
import com.mooc.entity.LocalAuth;
import com.mooc.entity.PersonInfo;
import com.mooc.util.DESUtil;
import com.mooc.util.MD5;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class LocalAuthDaoTest extends BaseTest {

    @Autowired
    private LocalAuthDao localAuthDao;
    private static  final String username="testUsername3";
    private  static final String password="hhhh";

    @Test
    public void testInsertLocalAuth(){
        //新增一条平台账号信息
        LocalAuth localAuth=new LocalAuth();
        PersonInfo personInfo=new PersonInfo();
        personInfo.setUserId(1L);
        //给平台账号绑定上用户信息
        localAuth.setPersonInfo(personInfo);
        //设置上用户名和密码
        localAuth.setUserName(username);
        localAuth.setPassword(DESUtil.getEncryptString(password));
        int insertLocalAuth = localAuthDao.insertLocalAuth(localAuth);
        assertEquals(1,insertLocalAuth);
    }

    @Test
    public void testQueryLocalByUserNameAndPwd(){
        //按照账号和密码查询用户信息
        LocalAuth localAuth = localAuthDao.queryLocalByUserNameAndPwd(username, password);
        assertEquals("测试",localAuth.getPersonInfo().getName());

    }

    @Test
    public void testQueryLocalByUserId(){
        //按照用户id查询平台账号，进而获取用户信息
        LocalAuth localAuth=localAuthDao.queryLocalByUserId(1L);
        assertEquals("测试",localAuth.getPersonInfo().getName());
    }

    @Test
    public void testUpdateLocalAuth(){
        //依据用户id，平台账号，以及旧密码修改平台账号密码
        Date now=new Date();
        int updateLocalAuth = localAuthDao.updateLocalAuth(1L, username, password, "heheheeh", now);
        assertEquals(1,updateLocalAuth);
       // System.out.println("");
    }

}
