package com.mooc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mooc.entity.Area;

import java.util.List;

public interface AreaService {

    public static final String AREALISTKEY="arealist";
    List<Area> getListArea();
}
