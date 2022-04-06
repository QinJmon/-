package com.mooc.service.impl;

import com.mooc.dao.ProductDao;
import com.mooc.dao.ProductImgDao;
import com.mooc.dto.ProductExecution;
import com.mooc.entity.Product;
import com.mooc.entity.ProductImg;
import com.mooc.enums.ProductStateEnum;
import com.mooc.exceptions.ProductOperationException;
import com.mooc.service.ProductService;
import com.mooc.util.ImageHolder;
import com.mooc.util.ImageUtil;
import com.mooc.util.PageCalculator;
import com.mooc.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductImgDao productImgDao;

    /*因为前端只认页数，dao只认行数，所以编写工具类进行转换*/
    public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
        //将pageIndex转为rowIndex
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        //返回需要的列表
        List<Product> productList = productDao.queryProductList(productCondition, rowIndex, pageSize);
        //获取总数
        int productCount = productDao.queryProductCount(productCondition);
        //将上面两个值赋给ProductExecution
        ProductExecution pe=new ProductExecution();
        if (productList!=null){
            pe.setProductList(productList);
            pe.setCount(productCount);
        }else {
            pe.setState(ProductStateEnum.EMPTY.getState());
        }

        return pe;
    }

    /*添加商品
    * 1、处理缩略图，获取缩略图的相对路径并赋值给peoduct
    * 2、向tb_product中写入商品信息，获取productId
    * 3、结合productId批量处理商品详细图
    * 4、将商品详情图列表插入到tb_product_img中*/
    @Transactional  //因为有四步操作，所以要加入事务管理
    public ProductExecution addProduct(Product product,
                                       ImageHolder thumbnail,
                                       List<ImageHolder> productImgHolderList)
            throws ProductOperationException {
        //标注该商品属于那个商铺
        if(product!=null && product.getShop()!=null && product.getShop().getShopId() !=null){
            //给商品设置上默认属性
            product.setCreateTime(new Date());
            product.setLastEditTime(new Date());
            //默认为上架的状态
            product.setEnableStatus(1);
            //如果商品缩略图不为空就添加
            if (thumbnail!=null){
                //添加缩略图
                addThumbnail(product,thumbnail);
            }
            try {
                int effectedNum = productDao.insertProduct(product);
                if (effectedNum<0){
                    throw new ProductOperationException("创建商品失败");
                }


            } catch (ProductOperationException e) {
                throw new ProductOperationException("创建商品失败"+e.toString());
            }
            //如果商品详情图不为空就添加
            if (productImgHolderList!=null && productImgHolderList.size()>0){
                addProductImgList(product,productImgHolderList);
            }
            return new ProductExecution(ProductStateEnum.SUCCESS,product);
        }else {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }

    }

    public Product getProductById(long productId) {
        return productDao.queryProductById(productId);
    }

    /*1、如果缩略图参数有值，就处理缩略图
    如果原先存在缩略图，就先删除缩略图，之后获取缩略图相对路径并赋值给product
    * 2、如果商品详情图列表有值，对商品详情图片做同样处理
    3、将tb_product_img 下面的噶商品原先的商品详情图记录全部清除
    4、更新tb_product的信息

    总之，更新商品信息因为牵涉到图片，就要把原先的图片给删除，再赋予新的图片*/
    public ProductExecution modifyProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgHolderList) throws ProductOperationException {
        //空值判断
        if(product!=null && product.getShop()!=null && product.getShop().getShopId()!=null){
            //给商品设置上默认属性
            product.setLastEditTime(new Date());
            //如果商品缩略图不为空删除原有缩略图并添加
            if(thumbnail!=null){
                //先获取一遍原有信息，因为原来的信息中有原有图片地址
                Product tempProduct=productDao.queryProductById(product.getProductId());
                if(tempProduct.getImgAddr()!=null){
                    ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
                }
                addThumbnail(product,thumbnail);

            }
            //如果有新存入的商品详情图，就将原来的删除并添加新的图片
            if(productImgHolderList!=null && productImgHolderList.size()>0){
                deleteProductImgList(product.getProductId());
                addProductImgList(product,productImgHolderList);
            }

            //更新商品信息
            try{
                int effectedNum=productDao.updateProduct(product);
                if(effectedNum<=0){
                    throw new ProductOperationException("更新图片信息失败");
                }
                return new ProductExecution(ProductStateEnum.SUCCESS,product);

            } catch (Exception e) {
                throw new ProductOperationException("更新图片信息失败："+e.toString());
            }
        }else {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
        
    }


    /*删除某个商品下面的所有详情图*/
    private void deleteProductImgList(Long productId) {
        //根据productId获取原来的图片
        List<ProductImg> productImgList=productImgDao.queryProductImgList(productId);
        //删除原来的图片
        for (ProductImg productImg : productImgList) {
            ImageUtil.deleteFileOrPath(productImg.getImgAddr());
        }

        //删除数据库原有的图片信息
        productImgDao.deleteProductImgByProductId(productId);
    }


    /*添加缩略图*/
    private void addThumbnail(Product product, ImageHolder thumbnail) {
        String dest= PathUtil.getShopImagePath(product.getShop().getShopId());
        String thumbnailAddr= ImageUtil.generateThumbnail(thumbnail,dest);
        product.setImgAddr(thumbnailAddr);
    }

    /*批量添加图片*/
    private void addProductImgList(Product product, List<ImageHolder> productImgHolderList) {
        //获取图片存储路径，这里直接存储在相应店铺的文件夹下面
        String dest=PathUtil.getShopImagePath(product.getShop().getShopId());
        List<ProductImg> productImgList=new ArrayList<ProductImg>();
        //遍历图片依次去添加到productImg的实体类中
        for (ImageHolder productImageHolder : productImgHolderList) {
            //得到图片的相对路径
            String imgAddr=ImageUtil.generateNormalImg(productImageHolder,dest);
            ProductImg productImg=new ProductImg();
            productImg.setImgAddr(imgAddr);
            productImg.setProductId(product.getProductId());
            productImg.setCreateTime(new Date());
            productImgList.add(productImg);
        }
        //如果确实有图片进行添加的，就执行批量添加操作
        if(productImgList.size()>0){
            try{
                int effectedNum = productImgDao.batchInsertProductImg(productImgList);
                if(effectedNum<=0){
                    throw new ProductOperationException("创建商品详情图失败");
                }

            } catch (Exception e) {
                throw  new ProductOperationException("创建商品详情图失败："+e.toString());
            }
        }
    }
}
