package com.mooc.exceptions;

public class HeadLineOperationException extends RuntimeException {

    /*对事物的操作，保证事务的一致性，出错回滚数据*/
    public HeadLineOperationException(String msg){
        super(msg);
    }


}
