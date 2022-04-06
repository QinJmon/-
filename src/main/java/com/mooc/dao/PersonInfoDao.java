package com.mooc.dao;

import com.mooc.entity.PersonInfo;

public interface PersonInfoDao {
    /*
     * @param      wechatAuth
     * @return
     */
    int insertPersonInfo(PersonInfo personInfo);
}
