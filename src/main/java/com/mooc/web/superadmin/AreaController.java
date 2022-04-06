package com.mooc.web.superadmin;

import com.mooc.entity.Area;
import com.mooc.service.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/superadmin")
public class AreaController {

    Logger logger= LoggerFactory.getLogger(AreaController.class);

    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/listArea")
    @ResponseBody  //将结果转为json对象
    public Map<String,Object> listArea() throws Exception {
        logger.info("=====start===");
        long startTime=System.currentTimeMillis();

        Map<String,Object> modelMap=new HashMap<String, Object>();
        List<Area> Areas = areaService.getListArea();
        modelMap.put("rows",Areas);
        modelMap.put("total",Areas.size());


        long endTime=System.currentTimeMillis();
        logger.debug("costTime:[{}ms]",endTime-startTime);
        logger.info("=====end===");
        return modelMap;
    }
}
