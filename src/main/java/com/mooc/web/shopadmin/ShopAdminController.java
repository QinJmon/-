package com.mooc.web.shopadmin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/shopadmin",method = {RequestMethod.GET})
public class ShopAdminController {
/*web-inf下面的目录不能在地址栏输入直接访问，需要后台转发*/
    @RequestMapping(value = "/shopoperation")
    public String shopoperation(){
        return "shop/shopoperation"; //web-inf下的shop文件夹下的shopoperation.html
    }


    @RequestMapping("/shoplist")
    public String shopList() {
        return "shop/shoplist";  //web-inf下的shop文件夹下的shoplist.html

    }

    @RequestMapping(value="/shopmanagement")
    public String shopManagement(){
        return "shop/shopmanagement";   //访问web-inf下的shop文件夹下的shopmanagement.html
    }

    @RequestMapping(value="/productcategorymanage",method = RequestMethod.GET)
    public String productCategoryManagement(){
        return "shop/productcategorymanage";   //访问web-inf下的shop文件夹下的productcategorymanagement.html
    }

    @RequestMapping(value = "/productoperation")
    public String productOperation(){
        return "shop/productoperation";
    }


    @RequestMapping(value = "/productmanage")
    public String productproductmanage(){
        return "shop/productmanagement";
    }

}
