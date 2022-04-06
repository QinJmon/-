package com.mooc.dao;

import com.mooc.BaseTest;
        import com.mooc.entity.Area;
        import org.junit.Test;
        import org.springframework.beans.factory.annotation.Autowired;

        import java.util.List;

public class AreaDaoTest extends BaseTest {

    @Autowired
    private AreaDao areaDao;

    @Test
    public void testFindAll(){
        List<Area> areaList = areaDao.findAll();
        System.out.println(areaList);
    }
}
