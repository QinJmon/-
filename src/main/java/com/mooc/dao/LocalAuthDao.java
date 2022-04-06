package com.mooc.dao;

import com.mooc.dto.LocalAuthExection;
import com.mooc.entity.LocalAuth;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface LocalAuthDao {
    /*通过账号和密码查询对应信息，登陆用*/
    LocalAuth queryLocalByUserNameAndPwd(@Param("username")String username,
                                         @Param("password")String password);

    /*根据用户id查找对应的localauth*/
    LocalAuth queryLocalByUserId(@Param("userId")long userId);

    /*添加平台账号*/
    int insertLocalAuth(LocalAuth localAuth);

    /*通过userid，username，password更改密码*/
    int updateLocalAuth(@Param("userId")Long userId, @Param("username")String username,
                        @Param("password")String password, @Param("newPassword")String newPassword,
                        @Param("lastEditTime")Date lastEditTime);



}
