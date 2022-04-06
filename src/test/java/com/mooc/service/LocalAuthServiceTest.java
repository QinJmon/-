package com.mooc.service;

import com.mooc.BaseTest;
import com.mooc.dto.LocalAuthExection;
import com.mooc.entity.LocalAuth;
import com.mooc.entity.PersonInfo;
import com.mooc.enums.LocalAuthStateEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class LocalAuthServiceTest extends BaseTest {

    @Autowired
    private LocalAuthService localAuthService;

    @Test
    public void testbindLocalAuth(){
        //先增加一条数据
        LocalAuth localAuth=new LocalAuth();
        localAuth.setUserName("hahahaha");
        localAuth.setPassword("hehehehe");
        PersonInfo personInfo=new PersonInfo();
        personInfo.setUserId(1L);
        localAuth.setPersonInfo(personInfo);
        //绑定账号
        LocalAuthExection lae = localAuthService.bindLocalAuth(localAuth);
       //通过userid找到新增的localauth
        LocalAuth auth = localAuthService.getLocalAuthByUserId(personInfo.getUserId());
        System.out.println("用户昵称："+localAuth.getPersonInfo().getName());
        System.out.println("平台账号密码："+localAuth.getPassword());
    }

    @Test
    public void testmodifyLocalAuth(){

        String username="test1";
        String password="test1";
        String newPassword="tt";
        //修改该账号对应的密码
        LocalAuthExection lae = localAuthService.modifyLocalAuth(1L, username, password,
                newPassword, new Date());
        assertEquals(LocalAuthStateEnum.SUCCESS.getState(),lae.getState());
        //通过账号密码找到修改后的localAuth
        LocalAuth auth = localAuthService.getLocalAuthByUsernamAndPwd(username, newPassword);
        //打印用户名字
        System.out.println(auth.getPersonInfo().getName());
        System.out.println("平台账号密码："+auth.getPassword());

    }
}
