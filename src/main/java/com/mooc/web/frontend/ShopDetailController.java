package com.mooc.web.frontend;

import com.mooc.dto.ProductExecution;
import com.mooc.entity.Product;
import com.mooc.entity.ProductCategory;
import com.mooc.entity.Shop;
import com.mooc.service.ProductCategoryService;
import com.mooc.service.ProductService;
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
public class ShopDetailController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;

    /*获得店铺信息以及该店铺下面的商品类别列表*/
    @RequestMapping(value = "/listshopdetailpageinfo",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> listShopDetailPageInfo(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String, Object>();
        //1。先取出shopId
        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        //2.再根据shopId取出下面的商铺信息
        if(shopId!=-1){
            Shop shop = shopService.getByShopId(shopId);
            //3。再获取该商铺下的商品类别信息
            List<ProductCategory> productCategoryList = productCategoryService.getProductCategoryList(shopId);
            // 4.将他们存入到键值对中
            modelMap.put("shop",shop);
            modelMap.put("productCategoryList",productCategoryList);
            modelMap.put("success",true);
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty shopId");
        }
        return modelMap;

    }

    /*依据查询条件分页列出该店铺下面的所有商品*/
    @RequestMapping(value = "/listproductsbyshop",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> listProductsByShop(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String, Object>();
        //获取页码
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        //获取一页需要展示的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        //获取当前店铺
        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        ////非空判断
        if(shopId >-1 && pageIndex>-1 && pageSize>-1){
            //试着获取商品类别
            long productCategoryId = HttpServletRequestUtil.getLong(request, "productCategoryId");
            //试着获取商品名称
            String productName = HttpServletRequestUtil.getString(request, "productName");
            //组合条件查询
            Product productCondition=compactShopCondition4Search(shopId,productCategoryId,productName);
            //获得查询的商品列表
            ProductExecution pe = productService.getProductList(productCondition,pageIndex,pageSize);
            //将其结果放入键值对中
            modelMap.put("productList",pe.getProductList());
            modelMap.put("count",pe.getCount());
            modelMap.put("success",true);

        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty pageSize or pageIndex or shopId");
        }

        return modelMap;
    }

    /*组合条件查询条件，封装到ProductCondition中*/
    private Product compactShopCondition4Search(long shopId, long productCategoryId, String productName) {
        Product productCondition=new Product();
        Shop shop=new Shop();
        shop.setShopId(shopId);
        productCondition.setShop(shop);

        if(productCategoryId!=-1){
            //查询某个商品类别下面的商品列表
            ProductCategory productCategory=new ProductCategory();
            productCategory.setProductCategoryId(productCategoryId);
            productCondition.setProductCategory(productCategory);
        }
        if(productName!=null){
            productCondition.setProductName(productName);
        }
        //只允许选出状态为上架的商品
        productCondition.setEnableStatus(1);
        return productCondition;
    }
}
