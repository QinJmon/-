package com.mooc.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mooc.dto.ShopExecution;
import com.mooc.entity.Area;
import com.mooc.entity.PersonInfo;
import com.mooc.entity.Shop;
import com.mooc.entity.ShopCategory;
import com.mooc.enums.ShopStateEnum;
import com.mooc.service.AreaService;
import com.mooc.service.ShopCategoryService;
import com.mooc.service.ShopService;
import com.mooc.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private AreaService areaService;

    /*不经过登录或者从商品列表中去的，视为违规操作，重定向回去*/
    @RequestMapping(value = "/getshopmanagentinfo",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getShopManagentInfo(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //获取店铺id
        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        //如果前端没有传shopId，则从session中获取
        if(shopId<=0){
            Object currentShopObj=request.getSession().getAttribute("currentShop");
            //如果session也没有，就将其重定向到之前的页面
            if(currentShopObj==null){
                modelMap.put("redirect",true);
                modelMap.put("url","/o2o-1/shopadmin/shoplist");
            }else {
                //如果session中有
                Shop currentShop=(Shop)currentShopObj;
                modelMap.put("redirect",false);
                modelMap.put("shopId",currentShop.getShopId());
            }

        }else {
            //如果前端传来的有值
            Shop currentShop=new Shop();
            currentShop.setShopId(shopId);
            request.getSession().setAttribute("currentShop",currentShop);
            modelMap.put("redirect",false);
        }
        return modelMap;

    }

    /*根据用户信息取显示用户创建的店铺*/
    @RequestMapping(value = "/getshoplist",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getShopList(HttpServletRequest request){
        Map<String,Object> modelMap =new HashMap<String, Object>();
        // 现在还没有做登录模块，因此session中并没有用户的信息，先模拟一下登录 要改造TODO
        //获取用户信息
        /*PersonInfo user=new PersonInfo();
        user.setUserId(1L);
        user.setName("模拟用户");
        request.getSession().setAttribute("user",user);*/
       PersonInfo user=(PersonInfo)request.getSession().getAttribute("user");

        try {
            //获得个人创建的商铺列表
            Shop shopCondition = new Shop();
            shopCondition.setOwner(user);
            //因为个人创建的店铺比较少，所以不分页
            ShopExecution se = shopService.getShopList(shopCondition, 1, 100);
            modelMap.put("shopList",se.getShopList());
            modelMap.put("user",user);
            modelMap.put("success",true);
        }catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errMsg",e.getMessage());
        }
        return modelMap;

    }


    @RequestMapping(value = "/getshopbyid",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getShopById(HttpServletRequest request){
        Map<String,Object> modelMap =new HashMap<String, Object>();
       long shopId = HttpServletRequestUtil.getLong(request, "shopId");
       // long shopId =1;
        if(shopId>-1){
            try {
                Shop shop = shopService.getByShopId(shopId);
                List<Area> areaList = areaService.getListArea();
                modelMap.put("shop", shop);
                modelMap.put("areaList", areaList);
                modelMap.put("success", true);
            }catch (Exception e){
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
            }

        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty shodId");
        }

        return modelMap;
    }


    //获得店铺类别的列表和区域列表赋值到modelMap中去，转换成json将其返回到前台
    @RequestMapping(value = "/getshopinitinfo" ,method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getShopInitInfo(){
        Map<String,Object> modelMap =new HashMap<String, Object>();
        List<ShopCategory> shopCategoryList=new ArrayList<ShopCategory>();
        List<Area> areaList=new ArrayList<Area>();
        try{
           shopCategoryList=shopCategoryService.getShopCategoryList(new ShopCategory());
           areaList=areaService.getListArea();
           modelMap.put("shopCategoryList",shopCategoryList);
           modelMap.put("areaList",areaList);
           modelMap.put("success",true);
        }catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errMsg",e.getMessage());
        }
        return modelMap;
    }



    @RequestMapping(value = "/registshop",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> registerShop(HttpServletRequest request){
        Map<String,Object> modelMap =new HashMap<String, Object>();
        if (!CodeUtil.checkVerifyCode(request)){
            modelMap.put("seuccess",false);
            modelMap.put("errMsg","输入了错误的验证码");
            return modelMap;
        }

        //1.接收并转化相应的参数，包括店铺信息以及图片信息将json转化为实体类）
        //shopStr是与前端约定的，request是json格式
        String shopStr= HttpServletRequestUtil.getString(request,"shopStr");
        ObjectMapper mapper=new ObjectMapper();
        Shop shop=null;
        try{
            //将传入的jsonshopStr转为Shop对象并完成赋值
            shop=mapper.readValue(shopStr,Shop.class);
        }catch (Exception e){
            modelMap.put("seuccess",false);
            modelMap.put("errMsg",e.getMessage());
            return modelMap;
        }
        // 1.2 图片信息 基于Apache Commons FileUpload的文件上传
        //CommonsMultipartFile用来处理图片，获取前端传过来的文件流，转换为shopImg
        CommonsMultipartFile shopImg=null;
        //获取文件内容
        CommonsMultipartResolver commonsMultipartResolver=new CommonsMultipartResolver(request.getSession().getServletContext());
        //如果有上传的文件流，转换为相应的文件，没有则报错
        if(commonsMultipartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartHttpServletRequest=(MultipartHttpServletRequest)request;
            // shopImg是和前端约定好的变量名
            shopImg=(CommonsMultipartFile)multipartHttpServletRequest.getFile("shopImg");
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","上传的图片不能为空");
            return modelMap;
        }
        //2.注册店铺
        if(shop !=null && shopImg!=null){
            //获取用户信息
            PersonInfo owner=(PersonInfo)request.getSession().getAttribute("user");
            shop.setOwner(owner);
            ShopExecution se;

            try {
                ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(),shopImg.getInputStream());
                se = shopService.addShop(shop,imageHolder);
                //判断返回值状态,se初始化
                if(se.getState()== ShopStateEnum.CHECK.getState()){
                    modelMap.put("success",true);
                    //一个用户可以操作多个商铺，将他可以操作的商铺列出来
                    @SuppressWarnings("unchecked")
                    List<Shop> shopList=(List<Shop>)request.getSession().getAttribute("shopList");
                    //如果这时她第一个创建的商铺,将他创建出来
                    if(shopList==null ||shopList.size()==0){
                        shopList=new ArrayList<Shop>();
                    }
                    shopList.add(se.getShop());
                    request.getSession().setAttribute("shopList",shopList);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errorMsg",se.getStateInfo());

                }
            } catch (IOException e) {
                modelMap.put("success",false);
              //  modelMap.put("errorMsg",se.getStateInfo());

            }

            return modelMap;
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入店铺信息");
            return modelMap;
        }



    }

    @RequestMapping(value = "/modifyshop",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> modifyShop(HttpServletRequest request){
        Map<String,Object> modelMap =new HashMap<String, Object>();
        if (!CodeUtil.checkVerifyCode(request)){
            modelMap.put("seuccess",false);
            modelMap.put("errMsg","输入了错误的验证码");
            return modelMap;
        }

        //1.接收并转化相应的参数，包括店铺信息以及图片信息
        String shopStr= HttpServletRequestUtil.getString(request,"shopStr");
        // 使用jackson-databind 将json转换为pojo
        ObjectMapper mapper=new ObjectMapper();
        Shop shop=null;
        try{
            // 将json转换为pojo
            shop=mapper.readValue(shopStr,Shop.class);
        }catch (Exception e){
            modelMap.put("seuccess",false);
            modelMap.put("errMsg",e.getMessage());
            return modelMap;
        }
        CommonsMultipartFile shopImg=null;
        //获取文件内容
        CommonsMultipartResolver commonsMultipartResolver=new CommonsMultipartResolver(request.getSession().getServletContext());
        //如果有上传的文件流，转换为相应的文件，没有则报错
        if(commonsMultipartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartHttpServletRequest=(MultipartHttpServletRequest)request;
            shopImg=(CommonsMultipartFile)multipartHttpServletRequest.getFile("shopImg");
        }
        //2.修改店铺
        if(shop !=null && shop.getShopId()!=null){
            // Session 部分的 PersonInfo 修改商铺是不需要的设置的。
            // 修改店铺
            ShopExecution se;
            try {
                if (shopImg != null) {
                    ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(),shopImg.getInputStream());
                    se = shopService.modifyShop(shop,imageHolder);
                }else {
                    se = shopService.modifyShop(shop,null);
                }
                //判断返回值状态,se初始化
                if(se.getState()== ShopStateEnum.Success.getState()){
                    modelMap.put("success",true);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errorMsg",se.getStateInfo());

                }
            } catch (IOException e) {
                modelMap.put("success",false);
                //  modelMap.put("errorMsg",se.getStateInfo());

            }

            return modelMap;
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入店铺Id");
            return modelMap;
        }



    }
   /* private  static void inputStreamToFile(InputStream ins, File file){
        FileOutputStream os=null;
        try{
            os=new FileOutputStream(file);
            int bytesRead=0;
            byte[] buffer=new byte[1024];
            while((bytesRead=ins.read(buffer))!=-1){
                os.write(buffer,0,bytesRead);
            }

        } catch (Exception e) {
            throw new RuntimeException("调用inputStreamToFile产生异常:"+e.getMessage());
        }finally {
            try {
                os.close();
                ins.close();
            } catch (IOException e) {
                throw new RuntimeException("inputStreamToFile关闭io异常"+e.getMessage());
            }

        }
    }*/
}
