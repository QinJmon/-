package com.mooc.dao;

import com.mooc.entity.Area;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AreaDao {


    List<Area> findAll();
}
