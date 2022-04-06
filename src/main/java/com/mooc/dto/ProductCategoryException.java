package com.mooc.dto;

import com.mooc.entity.ProductCategory;
import com.mooc.enums.ProductCategoryStateEnum;

import java.util.List;

/*用来返回商品类别的一些信息得类*/
public class ProductCategoryException {
    private int state; //结果值
    private String stateInfo;  //状态标识

    //以及批量处理添加商品类别的集合
    private List<ProductCategory> productCategoryList;

    public ProductCategoryException() {
    }

    //操作成功时使用的构造器
    public ProductCategoryException(ProductCategoryStateEnum stateEnum,List<ProductCategory> productCategoryList){
        this.state =stateEnum.getState();
        this.stateInfo =stateEnum.getStateInfo();
        this.productCategoryList=productCategoryList;
    }


    //操作失败时使用的构造器
    public ProductCategoryException(ProductCategoryStateEnum stateEnum) {
        this.state =stateEnum.getState();
        this.stateInfo =stateEnum.getStateInfo();
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

    public List<ProductCategory> getProductCategoryList() {
        return productCategoryList;
    }

    public void setProductCategoryList(List<ProductCategory> productCategoryList) {
        this.productCategoryList = productCategoryList;
    }
}
