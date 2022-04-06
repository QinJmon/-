package com.mooc.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mooc.dto.ProductExecution;
import com.mooc.entity.Product;
import com.mooc.entity.ProductCategory;
import com.mooc.entity.Shop;
import com.mooc.enums.ProductStateEnum;
import com.mooc.exceptions.ProductOperationException;
import com.mooc.service.ProductCategoryService;
import com.mooc.service.ProductService;
import com.mooc.util.CodeUtil;
import com.mooc.util.HttpServletRequestUtil;
import com.mooc.util.ImageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shopadmin")
public class ProductManagementController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryService productCategoryService;

    //支持上传商品详情图的最大数量
    private  static final int IMAGEMAXCOUNT = 6;

    @RequestMapping(value = "/getproductlistbyshop",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getProductListByShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //获取前台传来的页码
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        //获取前台传来的每页要求返回的商品数
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        //从当前session中获取店铺信息，主要获取shopId
        Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");
        Long shopId = currentShop.getShopId();
        //空值判断
        if((pageIndex>-1) && (pageSize>-1) && (currentShop!=null) && shopId!=null){
            //获取传入的需要检索的条件，包括是否需要从某个商品类别以及模糊查找商品名去筛选某个商铺下的商品列表
            //筛选的条件进行排列组合
            long productCategoryId = HttpServletRequestUtil.getLong(request, "productCategoryId");
            String productName = HttpServletRequestUtil.getString(request, "productName");
            Product productCondition=compactProductCondition(shopId,productCategoryId,productName);
            //传入查询条件以及分页信息进行查询，返回相应商品列表以及总数
            ProductExecution pe = productService.getProductList(productCondition, pageIndex, pageSize);
            modelMap.put("productList",pe.getProductList());
            modelMap.put("count",pe.getCount());
            modelMap.put("success",true);

        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty pageSize or pageIndex or ShopId");
        }
        return modelMap;
    }

    /*将查询商品条件进行整合*/
    private Product compactProductCondition(Long shopId, long productCategoryId, String productName) {
        Product productCondition=new Product();
        Shop shop=new Shop();
        shop.setShopId(shopId);
        productCondition.setShop(shop);

        //若有指定类别的要求则添加进去
        if(productCategoryId!=-1L){
            ProductCategory productCategory=new ProductCategory();
            productCategory.setProductCategoryId(productCategoryId);
            productCondition.setProductCategory(productCategory);
        }

        //若有商品名模糊查询的要求则添加进去
        if(productName!=null){
            productCondition.setProductName(productName);
        }
        return productCondition;
    }


    @RequestMapping(value = "/addproduct",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> addProduct(HttpServletRequest request) throws IOException {
        Map<String,Object> modelMap=new HashMap<String, Object>();
        //验证码校验
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入了错误的验证码");
            return modelMap;
        }

        //接受前端参数的变量的初始化，包括商品，缩略图，详情图列表实体类
        //1.图片
        MultipartHttpServletRequest multipartRequest=null; //用来处理文件流
        ImageHolder thumbnail=null; //保存缩略图的文件流和名称
        List<ImageHolder> productImgList=new ArrayList<ImageHolder>(); //保存详情图的文件流和名称
        //从session中获取文件流
        CommonsMultipartResolver multipartResolver=new
                CommonsMultipartResolver(request.getSession().getServletContext());
       try {
           //当请求中有文件流时
           if (multipartResolver.isMultipart(request)) {
               multipartRequest = (MultipartHttpServletRequest) request;
               //取出缩略图并构建ImageHolder对象
               CommonsMultipartFile thumbnailFile =
                       (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
               thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
               //取出详情图列表并构建List<>列表对象，最多支持6张图片上传
               for (int i = 0; i < IMAGEMAXCOUNT; i++) {
                   CommonsMultipartFile productImgFile =
                           (CommonsMultipartFile) multipartRequest.getFile("productImg" + i);//和前端约定好传来的key就是"productImg"+i
                   if (productImgFile != null) {
                       //如果取出来的第i个详情图片不为空，那么将其加入详情图列表
                       ImageHolder productImg = new
                               ImageHolder(productImgFile.getOriginalFilename(), productImgFile.getInputStream());
                       productImgList.add(productImg);

                   } else {
                       //若果取出来的第i个详情图片文件为空，则无文件再上传，终止循环
                       break;
                   }
               }

           } else {
               //当请求中没有文件流
               modelMap.put("success", false);
               modelMap.put("errMsg", "上传图片不能为空");
               return modelMap;
           }
       } catch (IOException e) {
           modelMap.put("success", false);
           modelMap.put("errMsg", e.toString());
           return modelMap;
       }

       //2.商品实体类
        ObjectMapper mapper=new ObjectMapper(); //是Jackson的主要类，它可以帮助我们快速的进行各个类型和Json类型的相互转换。
        Product product=null;
        String productStr= HttpServletRequestUtil.getString(request,"productStr"); //和前端约定好的key
        try{
            //尝试获取前台传来的表单将其转换为product实体类
            product = mapper.readValue(productStr, Product.class);

        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }

        //进行商品添加工作
        if(product!=null && thumbnail !=null && productImgList.size()>0){
            try {
                //从当前店铺中获取店铺的id并复制给product，减少对前端数据的依赖
                Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
                Shop shop = new Shop();
                shop.setShopId(currentShop.getShopId());
                product.setShop(shop);

                //返回实体类的实例化
                ProductExecution pe = productService.addProduct(product, thumbnail, productImgList);

                if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", pe.getStateInfo());
                }
            } catch (ProductOperationException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
                return modelMap;
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入商品信息");
        }

        return modelMap;
    }

    @RequestMapping(value = "/getproductbyid",method = RequestMethod.GET)
    @ResponseBody     ///@RequestParam????????
    public Map<String,Object> getProductById(@RequestParam Long productId){
        Map<String,Object> modelMap=new HashMap<String, Object>();
        //非空判断
        if(productId>-1){
            //获取商品信息
            Product product=productService.getProductById(productId);
            //获取该店铺下的商品类别
            List<ProductCategory> productCategoryList =
                    productCategoryService.getProductCategoryList(product.getShop().getShopId());
            modelMap.put("product",product);
            modelMap.put("productCategoryList",productCategoryList);
            modelMap.put("success",true);

        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty producted");
        }
        return modelMap;
    }

    @RequestMapping(value = "/modifyproduct",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> modifyProduct(HttpServletRequest request){
        Map<String,Object> modelMap=new HashMap<String, Object>();
        //判断是商品编辑还是商品上下架
        boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
        //如果为商品编辑就进行验证码判断，后者不进行
        if(!statusChange && !CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入了错误的验证码");
            return modelMap;
        }

        //接受前端参数的变量的初始化，包括商品，缩略图，详情图列表实体类
        //1.图片
        MultipartHttpServletRequest multipartRequest=null;
        ImageHolder thumbnail=null;
        List<ImageHolder> productImgList=new ArrayList<ImageHolder>();
        //从session中获取文件流
        CommonsMultipartResolver multipartResolver=new
                CommonsMultipartResolver(request.getSession().getServletContext());
        try{
            //判断请求中是否有文件流
            //如果有，则取出相关文件  （包括缩略图和详情图）
            if (multipartResolver.isMultipart(request)) {
                multipartRequest = (MultipartHttpServletRequest) request;
                //取出缩略图并构建ImageHolder对象
                CommonsMultipartFile thumbnailFile =
                        (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
                thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
                //取出详情图列表并构建List<>列表对象，最多支持6张图片上传
                for (int i = 0; i < IMAGEMAXCOUNT; i++) {
                    CommonsMultipartFile productImgFile =
                            (CommonsMultipartFile) multipartRequest.getFile("productImg" + i);//和前端约定好传来的key就是"productImg"+i
                    if (productImgFile != null) {
                        //如果取出来的第i个详情图片不为空，那么将其加入详情图列表
                        ImageHolder productImg = new
                                ImageHolder(productImgFile.getOriginalFilename(), productImgFile.getInputStream());
                        productImgList.add(productImg);

                    } else {
                        //若果取出来的第i个详情图片文件为空，则无文件再上传，终止循环
                        break;
                    }
                }

            }
            //当请求中没有文件流不做处理

        } catch (Exception e) {
            modelMap.put("success",false);
            modelMap.put("errMsg",e.toString());
            return modelMap;
        }

        //商品实体类
        ObjectMapper mapper=new ObjectMapper(); //是Jackson的主要类，它可以帮助我们快速的进行各个类型和Json类型的相互转换。
        Product product=null;
        String productStr= HttpServletRequestUtil.getString(request,"productStr"); //和前端约定好的key
        try{
            //尝试获取前台传来的表单将其转换为product实体类
            product = mapper.readValue(productStr, Product.class);

        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }

        //进行商品更新
        if(product!=null){
            try {
                //从当前店铺中获取店铺的id并复制给product，减少对前端数据的依赖
                Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
                Shop shop = new Shop();
                product.setShop(currentShop);

                //开始对商品信息变更操作
                ProductExecution pe = productService.modifyProduct(product, thumbnail, productImgList);

                if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", pe.getStateInfo());
                }
            } catch (ProductOperationException e) {
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
                return modelMap;
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入商品信息");
        }
        return modelMap;
    }


}
