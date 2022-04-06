package com.mooc.dao;

import com.mooc.BaseTest;
import com.mooc.entity.HeadLine;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class HeadLineDaoTest extends BaseTest {
    @Autowired
    private HeadLineDao headLineDao;

    @Test
    public void testqueryHeadLine(){
        List<HeadLine> headLines = headLineDao.queryHeadLine(new HeadLine());
        assertEquals(1,headLines.size());

    }
}
