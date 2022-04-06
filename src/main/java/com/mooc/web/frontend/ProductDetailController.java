package com.mooc.web.frontend;

import com.mooc.entity.Product;
import com.mooc.service.ProductService;
import com.mooc.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/frontend")
public class ProductDetailController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/listproductdetailpageinfo",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> showProductDetail(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String, Object>();
        //获取前台传过来的productId
        long productId = HttpServletRequestUtil.getLong(request, "productId");
        //Product product=null;
        if(productId!=-1){
            Product product = productService.getProductById(productId);
            modelMap.put("success",true);
            modelMap.put("product",product);
        }else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty productId");
        }

        return modelMap;
    }
}
