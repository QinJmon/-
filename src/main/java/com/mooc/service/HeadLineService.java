package com.mooc.service;

import com.mooc.entity.HeadLine;

import java.util.List;

public interface HeadLineService {

    public static final String HLLISTKEY="headlinelist";
    /*根据传入的条件返回指定的头条列表*/
    List<HeadLine> getHeadLineList(HeadLine headLineCondition);
}
