package com.mooc.exceptions;

public class ShopCategoryOperationException extends RuntimeException {

    /*对事物的操作，保证事务的一致性，出错回滚数据*/
    public ShopCategoryOperationException(String msg){
        super(msg);
    }


}
