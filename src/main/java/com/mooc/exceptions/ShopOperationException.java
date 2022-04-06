package com.mooc.exceptions;

public class ShopOperationException extends RuntimeException {

    /*对事物的操作，保证事务的一致性，出错回滚数据*/
    public ShopOperationException(String msg){
        super(msg);
    }


}
