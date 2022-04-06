package com.mooc.util;

public class PathUtil {
    //获得系统的分隔符
    private static String separator=System.getProperty("file.separator");

    //返回项目的根路径
    public static String getImgBasePath(){
        //获得系统名称
        String os=System.getProperty("os.name");
        String basePath="";
        if (os.toLowerCase().startsWith("win")){
            basePath="C:/XiaoYuanShangPu/image";
        }else {
            basePath="/home/XiaoYuanShangPu/image";
        }
        basePath=basePath.replace("/",separator);
        return basePath;
    }

    //根据项目返回图片的子路径
    public static String getShopImagePath(Long shopId){
        String imagePath="/upload/item/shop/"+shopId +"/";
        return imagePath.replace("/",separator);
    }
}
