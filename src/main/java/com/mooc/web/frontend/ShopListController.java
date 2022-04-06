package com.mooc.web.frontend;

import com.mooc.dto.ShopExecution;
import com.mooc.entity.Area;
import com.mooc.entity.Shop;
import com.mooc.entity.ShopCategory;
import com.mooc.service.AreaService;
import com.mooc.service.ShopCategoryService;
import com.mooc.service.ShopService;
import com.mooc.util.HttpServletRequestUtil;
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
public class ShopListController {

    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/listshoppageinfo",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> listShopPageInfo(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String, Object>();
        //试着从前端请求中获取parentId
        long parentId = HttpServletRequestUtil.getLong(request, "parentId");
        List<ShopCategory> shopCategoryList =null;
        /*
        它分为两级，一级是商铺的总类，二级是商铺的小范围，
         然后我们现在就是根据一级商铺的id，去找二级商铺，找到后，再根据二级商铺的id返回我们要的该类别下的商铺列表*/
        if(parentId!=-1){
            try{
                //如果parentId存在，说明点击的不是全部商品，则取出一级ShopCategory下的二级ShopCategory列表
                ShopCategory shopCategoryCondition=new ShopCategory();
                ShopCategory parent=new ShopCategory();
                parent.setShopCategoryId(parentId);
                shopCategoryCondition.setParent(parent);
                 shopCategoryList = shopCategoryService.getShopCategoryList(shopCategoryCondition);
            } catch (Exception e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
            }
        }else {
            try{
                //如果parentId不存在（说明电机的是全部商品），则取出所有一级ShopCategory
                 shopCategoryList = shopCategoryService.getShopCategoryList(null);

            } catch (Exception e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
            }
        }
        modelMap.put("shopCategoryList",shopCategoryList);
        //取出区域信息
        try {
            List<Area> areaList = areaService.getListArea();
            modelMap.put("areaList", areaList);
            modelMap.put("success",true);
            return modelMap;
        } catch (Exception e) {
            modelMap.put("success",false);
            modelMap.put("errMsg",e.getMessage());
        }
        return modelMap;

    }


    @RequestMapping(value = "/listshops",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> listShops(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String, Object>();
        //获取页码
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        //获取一页需要显示的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        //非空判断
        if(pageIndex>-1 && pageSize>-1){
            //试着获取一级类别Id
            long parentId = HttpServletRequestUtil.getLong(request, "parentId");
            //试着获取特定二级类别id
            long shopCategoryId = HttpServletRequestUtil.getLong(request, "shopCategoryId");
            //试着获取区域id
            int areaId = HttpServletRequestUtil.getInt(request, "areaId");
            //试着获取模糊查询的名字
            String shopName = HttpServletRequestUtil.getString(request, "shopName");
            //获取组合之后的查询条件
            Shop shopCondition=compactShopCondition4Search(parentId,shopCategoryId,areaId,shopName);
            //根据查询条件和分页信息获取店铺信息，并返回条数
            ShopExecution se = shopService.getShopList(shopCondition, pageIndex, pageSize);
            modelMap.put("shopList",se.getShopList());
            modelMap.put("count",se.getCount());
            modelMap.put("success",true);


        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty pageSize or pageIndex");
        }
        return modelMap;

    }

    /*组合查询条件*/
    private Shop compactShopCondition4Search(long parentId, long shopCategoryId, int areaId, String shopName) {
       Shop shopCondition=new Shop();
       //当点击的不是全部商品，获取二级商铺类别下的所有商铺
       if(parentId!=-1L){
           ShopCategory childCategory=new ShopCategory();
           ShopCategory parentCategory=new ShopCategory();
           parentCategory.setShopCategoryId(parentId);
           childCategory.setParent(parentCategory);
           shopCondition.setShopCategory(childCategory);
       }

       //根据二级商铺类别，查找下面的商铺信息
       if(shopCategoryId!=-1L){
           ShopCategory shopCategory=new ShopCategory();
           shopCategory.setShopCategoryId(shopCategoryId);
           shopCondition.setShopCategory(shopCategory);
       }
       //
        if(areaId!=-1){
            Area area=new Area();
            area.setAreaId(areaId);
            shopCondition.setArea(area);
        }

        if(shopName!=null){
            shopCondition.setShopName(shopName);

        }
        shopCondition.setEnableStatus(1);
        return shopCondition;
    }


}
