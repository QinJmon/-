package com.mooc.service.impl;


import com.mooc.dao.ProductCategoryDao;
import com.mooc.dao.ProductDao;
import com.mooc.dto.ProductCategoryException;
import com.mooc.entity.ProductCategory;
import com.mooc.enums.ProductCategoryStateEnum;
import com.mooc.exceptions.ProductCategoryOperationException;
import com.mooc.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryDao productCategoryDao;

    @Autowired
    private ProductDao productDao;

    public List<ProductCategory> getProductCategoryList(long shopId) {
        return productCategoryDao.queryProductCategory(shopId);
    }

    public ProductCategoryException batchAddProductCategory(List<ProductCategory> productCategoryList) throws ProductCategoryOperationException {
        //先判断传来的值是否符合条件
        if (productCategoryList != null && productCategoryList.size()>0) {
            try {
                int insertProductCategory = productCategoryDao.batchInsertProductCategory(productCategoryList);
                if (insertProductCategory <= 0) {
                    throw new ProductCategoryOperationException("店铺类别创建失败");
                } else {
                    return new ProductCategoryException(ProductCategoryStateEnum.SUCCESS);
                }
            }catch (Exception e){
                throw new ProductCategoryOperationException("batchAddProductCategory error:"+e.getMessage());
            }
        }else {
            return new ProductCategoryException(ProductCategoryStateEnum.EMPTY_LIST);
        }

    }


    //由于将来有两步操作，需要添加事务，当第一步成功后不急于提交，而是等第二步成功后再提交
    //如果第二步失败，事务就会回滚
    /*删除商品类别
     * 因为商品类别下面挂载的有商品，所以应该先让商品的id置为空，再删除掉商品类别*/
    @Transactional
    public ProductCategoryException deleteProductCategory(long productCategoryId, long shopId) throws ProductCategoryOperationException {
        //先解除商品与商品类别的关联
        try {
            //将次类别下的商品的类别id置为空
            int effectedNum = productDao.updateProductCategpryToNull(productCategoryId);
            if(effectedNum<0){
                throw new ProductCategoryOperationException("更新类别失败");
            }
        } catch (Exception e) {
            throw new ProductCategoryOperationException("deleteProductCategory error:"+e.toString());
        }
        //删除商品类别
        try{
            int i = productCategoryDao.deleteProductCategory(productCategoryId, shopId);
            if (i<=0){
                //删除失败
                throw new ProductCategoryOperationException("删除商品类别失败");
            }else {
                return new ProductCategoryException(ProductCategoryStateEnum.SUCCESS);
            }

        } catch (Exception e) {
            throw new ProductCategoryOperationException("deleteProductCategory error"+e.getMessage());
        }

    }
}
