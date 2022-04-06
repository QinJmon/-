package com.mooc.web.shopadmin;

import com.mooc.dto.ProductCategoryException;
import com.mooc.dto.Result;
import com.mooc.entity.ProductCategory;
import com.mooc.entity.Shop;
import com.mooc.enums.ProductCategoryStateEnum;
import com.mooc.exceptions.ProductCategoryOperationException;
import com.mooc.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/shopadmin")
public class ProductCategoryManagementController {

    @Autowired
    private ProductCategoryService productCategoryService;

    /*查询某个店铺下的商品类别*/
    @RequestMapping(value = "/getproductcategorylist",method = RequestMethod.GET)
    @ResponseBody //以json形式返回前端  ,因为返回的就一个参数，使用自己定义的Result
    public Result<List<ProductCategory>> getProductCategoryList(HttpServletRequest request){
        //从之前在商铺中存的session中获取shop
        Shop currentShop=(Shop)request.getSession().getAttribute("currentShop");
        List<ProductCategory> list=null;
        if(currentShop!=null && currentShop.getShopId()>0){
            list=productCategoryService.getProductCategoryList(currentShop.getShopId());
           //成功返回success的状态和数据
            return new Result<List<ProductCategory>>(true,list);
        }else {
            ProductCategoryStateEnum ps=ProductCategoryStateEnum.INNER_ERROR;
            return new Result<List<ProductCategory>>(false,ps.getState(),ps.getStateInfo());
        }



    }

    /*批量增加商品类别
    从前端传过来的productCategoryList, 所以使用@RequestBody
    * seesion中取出店铺id（尽可能少的依赖前端传来的数据），因为要将商品类别添加到某个商铺中
    * 先判断传来的list是否符合条件，再进行操作*/
    @RequestMapping(value = "/addproductcategory",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> addProductCategorys(@RequestBody List<ProductCategory> productCategoryList,
                                                  HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String, Object>();
        Shop currentShop=(Shop)request.getSession().getAttribute("currentShop");
        for (ProductCategory pc:productCategoryList ) {
            //给每一个商品类别添加当前商铺id
            pc.setShopId(currentShop.getShopId());
        }
        if(productCategoryList!=null && productCategoryList.size()>0){
            try{
                ProductCategoryException pe=productCategoryService.batchAddProductCategory(productCategoryList);
                if (pe.getState()==ProductCategoryStateEnum.SUCCESS.getState()){
                    modelMap.put("success",true);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg",pe.getStateInfo());
                }
            } catch (Exception e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
                return modelMap;
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请至少输入一个商品类别");
        }
        return modelMap;
    }

    /*删除某个商铺下的某个商品类别*/
    @RequestMapping(value = "/removeproductcategory",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> deleteProductCategory(Long productCategoryId,HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String, Object>();
        //获取当前session中的店铺id
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        Long shopId = currentShop.getShopId();
        //判断是否符合条件
        if (productCategoryId != null && productCategoryId >0 ) {
            ProductCategoryException pe = productCategoryService.deleteProductCategory(productCategoryId, shopId);
            try {
                //操作成功(返回的结果值等于状态枚举类中的success中的状态值)
                if (pe.getState() == ProductCategoryStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", pe.getStateInfo());
                }
            } catch (ProductCategoryOperationException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());

            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请至少选择一个商品类别");
        }
        return modelMap;
    }


}
