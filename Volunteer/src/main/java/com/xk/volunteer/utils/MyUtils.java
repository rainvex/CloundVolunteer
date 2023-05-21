package com.xk.volunteer.utils;

import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @Author Rainvex
 * @Date 2022/3/23 16:03
 */
public class MyUtils {
    /**
     * @author Rainvex
     * @date 2022/3/23 16:15
     * @method getCurrentTimestamp
     * @param []
     * @return java.sql.Timestamp
     * 获取一个当前时间戳以便存入数据库
     */
    public static Timestamp getCurrentTimestamp(){
        Date date = new Date();
        return new Timestamp(date.getTime());
    }

    /**
     * @author Rainvex
     * @date 2022/3/23 16:50
     * @method storeImage
     * @param [picture]
     * @return java.lang.String
     * 将传入的文件随机命名并上传文件，返回新生成的文件名
     */
    public static String storeHeadPic(MultipartFile picture){
        if (picture != null && StringUtils.hasLength(picture.getOriginalFilename())){
            try {
                String path = ResourceUtils.getURL("classpath:").getPath() + "static/assets/headPic";
                String uuid = UUID.randomUUID().toString();
                String suffix = picture.getOriginalFilename().substring(picture.getOriginalFilename().lastIndexOf("."));
                String filename = uuid + suffix;
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
                String savePic = path + "/" + filename;
                File savaPicture = new File(savePic);
                if (savaPicture.exists()){
                    System.out.println("文件已存在");
                    return "文件已存在";
                }
                picture.transferTo(savaPicture);
                return filename;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("未传入文件");
        return "未传入文件";
    }

    /**
     * @author Rainvex
     * @date 2022/4/5 19:41
     * @method storeActivePic
     * @param [picture]
     * @return java.lang.String
     * 将传入的文件随机命名并上传文件，返回新生成的文件名
     */
    public static String storeActivePic(MultipartFile picture){
        if (picture != null && StringUtils.hasLength(picture.getOriginalFilename())){
            try {
                String path = ResourceUtils.getURL("classpath:").getPath() + "static/assets/activePic";
                String uuid = UUID.randomUUID().toString();
                String suffix = picture.getOriginalFilename().substring(picture.getOriginalFilename().lastIndexOf("."));
                String filename = uuid + suffix;
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
                String savePic = path + "/" + filename;
                File savaPicture = new File(savePic);
                if (savaPicture.exists()){
                    System.out.println("文件已存在");
                    return "文件已存在";
                }
                picture.transferTo(savaPicture);
                return filename;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("未传入文件");
        return "未传入文件";
    }

    /**
     * @author Rainvex
     * @date 2022/4/12 19:32
     * @method dateToString
     * @param [timestamp]
     * @return java.lang.String
     */
    public static String dateToString(Timestamp timestamp){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(timestamp);
    }
}
