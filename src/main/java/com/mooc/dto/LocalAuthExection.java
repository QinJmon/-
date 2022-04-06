package com.mooc.dto;

import com.mooc.entity.LocalAuth;
import com.mooc.entity.Product;
import com.mooc.enums.LocalAuthStateEnum;
import com.mooc.enums.ProductStateEnum;

import java.util.List;

public class LocalAuthExection {
    //结果状态
    private int state;

    //状态标识
    private String stateInfo;

    //数量
    private int count;

    private  LocalAuth localAuth;

    private List<LocalAuth> localAuthList;

    public LocalAuthExection() {
    }

    //失败的构造器
    public LocalAuthExection(LocalAuthStateEnum stateEnum){
        this.state=stateEnum.getState();
        this.stateInfo=stateEnum.getStateInfo();
    }

    //成功的构造器(针对单个商品)
    public LocalAuthExection(LocalAuthStateEnum stateEnum, LocalAuth localAuth){
        this.state=stateEnum.getState();
        this.stateInfo=stateEnum.getStateInfo();
        this.localAuth=localAuth;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public LocalAuth getLocalAuth() {
        return localAuth;
    }

    public void setLocalAuth(LocalAuth localAuth) {
        this.localAuth = localAuth;
    }

    public List<LocalAuth> getLocalAuthList() {
        return localAuthList;
    }

    public void setLocalAuthList(List<LocalAuth> localAuthList) {
        this.localAuthList = localAuthList;
    }
}
