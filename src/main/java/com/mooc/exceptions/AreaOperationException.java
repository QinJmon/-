package com.mooc.exceptions;

public class AreaOperationException extends RuntimeException {

    /*对事物的操作，保证事务的一致性，出错回滚数据*/
    public AreaOperationException(String msg){
        super(msg);
    }


}
