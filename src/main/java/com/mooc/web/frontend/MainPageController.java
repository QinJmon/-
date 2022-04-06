package com.mooc.web.frontend;

import com.mooc.dao.HeadLineDao;
import com.mooc.dao.ShopCategoryDao;
import com.mooc.entity.HeadLine;
import com.mooc.entity.ShopCategory;
import com.mooc.service.HeadLineService;
import com.mooc.service.ShopCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/frontend")
public class MainPageController {

    @Autowired
    private ShopCategoryService shopCategoryService;

    @Autowired
    private HeadLineService headLineService;

    /*初始化页面，获取以及店铺类别列表和头条*/
    @RequestMapping(value = "/listmainpageinfo",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> listMainPageInfo(){
        Map<String,Object> modelMap=new HashMap<String, Object>();
        //获取一级店铺类别列表,即parentId为空的ShopCategory
        try {
            List<ShopCategory> shopCategoryList = shopCategoryService.getShopCategoryList(null);
            //将结果存到shopCategoryList里面好在前端获取
            modelMap.put("shopCategoryList",shopCategoryList);
        } catch (Exception e) {
            modelMap.put("success",false);
            modelMap.put("errMsg",e.toString());
            return modelMap;
        }
        try{
            //获取状态为可用1的头条列表信息
            HeadLine headLineCondition=new HeadLine();
            headLineCondition.setEnableStatus(1);
            List<HeadLine> headLineList = headLineService.getHeadLineList(headLineCondition);
            //将结果存到headLineList里面好在前端获取
            modelMap.put("headLineList",headLineList);

        } catch (Exception e) {
            modelMap.put("success",false);
            modelMap.put("errMsg",e.toString());
            return modelMap;
        }

        modelMap.put("success",true);
        return modelMap;
    }
}
