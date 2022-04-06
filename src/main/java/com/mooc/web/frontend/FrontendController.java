package com.mooc.web.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/frontend")
public class FrontendController {

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(){
        return "frontend/index";  //访问html文件下frontend文件夹下的index.html
    }

    @RequestMapping(value = "/shoplist", method = RequestMethod.GET)
    public String shopList() {
        return "frontend/shoplist";  //访问html文件下frontend文件夹下的shoplist.html
    }

    /*店铺详情页路径*/
    @RequestMapping(value = "/shopdetail", method = RequestMethod.GET)
    public String showShopDetail() {
        return "frontend/shopdetail";  //访问html文件下frontend文件夹下的shopdetail.html
    }


    /*商品详情页路径*/
    @RequestMapping(value = "/productdetail", method = RequestMethod.GET)
    public String productdetail() {
        return "frontend/productdetail";  //访问html文件下frontend文件夹下的shopdetail.html
    }

}
