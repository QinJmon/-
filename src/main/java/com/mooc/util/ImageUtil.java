package com.mooc.util;


import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ImageUtil {
    //String basePath=Thread.currentThread().getContextClassLoader().getResource("").getPath();
    private static String basePath="C:\\Users\\大 大\\Desktop\\About learn\\java学习\\代码\\IdeaProgrames\\SSM\\o2o-1\\src\\main\\resources\\watermark.jpg";
    private static final SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
    private static final Random r=new Random();
    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    //用来处理缩略图（参数用户传递过来的文件，和要存储的路径）   门面图
    //用户传来的参数可能会重名，我们用自己的
    public static String generateThumbnail(ImageHolder thumbnail, String targetAddr){
        String realFileName=getRandomFileName();
        String extension = getFileExtension(thumbnail.getImageName());
        makeDirPath(targetAddr);
        String relativeAddr = targetAddr + realFileName + extension;
        File dest=new File(PathUtil.getImgBasePath()+relativeAddr);
        try {
            Thumbnails.of(thumbnail.getImage()).size(200,200)
                    .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath)),0.5f)//添加水印
                    .outputQuality(0.7f).toFile(dest);//压缩图片
        } catch (IOException e) {
            throw new RuntimeException("创建缩略图失败：" + e.toString());
        }
        return relativeAddr;
    }

    //获取输入文件流的扩展名
    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));//获得最后.
    }

    //创建目标路径所设计到的目录，即/home/work/xiangze/xxx.jpg,
    // 那么home   work  xiangze 这三个文件夹都要自动创建
    private static void makeDirPath(String targetAddr) {
        String realFileParentPath=PathUtil.getImgBasePath()+targetAddr;
        File dirPath=new File(realFileParentPath);
        if (!dirPath.exists()){
            dirPath.mkdirs();
        }
    }

    //生成随机文件名，当前年月日消失分钟秒钟+随机五位数
    public static String getRandomFileName() {
        //获得随即五位数
        int rannum=r.nextInt(89999)+10000;
        String nowTimeStr=sDateFormat.format(new Date());
        return nowTimeStr + rannum;
    }

    /*跟新图片，删除原文件
    * storePath是文件的路径，则删除文件。如果是目录路径，则删除该目录下的所有文件*/
    public static void deleteFileOrPath(String storePath){
        //获得全路径
        File fileOrPath=new File(PathUtil.getImgBasePath()+storePath);
        if (fileOrPath.exists()){
            //删除目录下的所有文件
            if(fileOrPath.isDirectory()){
                File files[]=fileOrPath.listFiles();
                for (int i = 0; i <files.length ; i++) {
                    files[i].delete();
                }
            }
            fileOrPath.delete();
        }
    }

/*根据输入流，生成详情图片，并返回新生成图片的相对地址
* */
    public static String generateNormalImg(ImageHolder thumbnail,String targetAddr) {
        //获取文件的随机名称
        String realFileName=getRandomFileName();
        //获取用户上传的文件扩展名称（文件后缀）
        String extension=getFileExtension(thumbnail.getImageName());

        //创建图片的存储目录（这个目录包括根目录加上相对目录）
        makeDirPath(targetAddr);
        //图片的相对目录(带文件名)
        String relativeAddr = targetAddr +realFileName + extension;
        logger.debug("图片的相对路径："+relativeAddr);
        //图片的绝对目录
        File dest = new File(PathUtil.getImgBasePath() + relativeAddr);
        logger.debug("图片完整路径："+dest.getAbsolutePath());
        //调用 Thumbnails生成带有水印的图片
        try {
            //可以传入文件，也可以传入图片流
            Thumbnails.of(thumbnail.getImage()).size(337, 640)
                    .watermark(Positions.BOTTOM_RIGHT,ImageIO.read(new File(basePath)),0.25f)
                    .outputQuality(0.9f).toFile(dest);
        }catch (IOException e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
        return relativeAddr;

    }

    public static void main(String[] args) throws IOException {
        //获得当前线程的路径
        //String basePath=Thread.currentThread().getContextClassLoader().getResource("").getPath();
       // String basePath="C:\\Users\\大 大\\Desktop\\About learn\\java学习\\代码\\IdeaProgrames\\SSM\\o2o-1\\src\\main\\resources\\hh.png";
        //System.out.println(basePath);
        Thumbnails.of(new File("C:\\Users\\大 大\\Desktop\\About learn\\ps图片\\11.jpg")).size(200,200)
                .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath)),0.5f)//添加水印
                .outputQuality(0.7f).toFile("C:\\Users\\大 大\\Desktop\\About learn\\ps图片\\oo.jpg");//压缩图片
    }


}
