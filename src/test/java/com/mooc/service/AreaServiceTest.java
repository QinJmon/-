package com.mooc.service;

import com.mooc.BaseTest;
import com.mooc.entity.Area;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AreaServiceTest extends BaseTest {

    @Autowired
    private AreaService areaService;
    @Autowired
    private CacheService cacheService;

    @Test
    public void testgetListArea() throws Exception {

        List<Area> listArea = areaService.getListArea();
       assertEquals("东苑",listArea.get(0).getAreaName());
       //从缓存中移除key
       cacheService.removeFromCache(areaService.AREALISTKEY);
        listArea = areaService.getListArea();

    }
}
