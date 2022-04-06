package com.mooc.enums;
/*
* */
public enum ProductCategoryStateEnum {
    //状态
    SUCCESS(1,"创建成功"),
    INNER_ERROR(-1001,"操作失败"),
    EMPTY_LIST(-1002,"添加数少于1");

    private int state;
    private String stateInfo;

    //有参参数构造方法
    ProductCategoryStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }
    //只需要getter方法
    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    //状态值返回相对应的状态标识符
    public static ProductCategoryStateEnum stateOf(int index){
        for (ProductCategoryStateEnum  productCategoryStateEnum: values() ) {
            if(productCategoryStateEnum.getState()==index){
                return productCategoryStateEnum;
            }
        }
        return null;
    }

}
