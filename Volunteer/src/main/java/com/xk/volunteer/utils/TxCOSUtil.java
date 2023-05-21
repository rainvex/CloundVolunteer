package com.xk.volunteer.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;

/**
 * @Author Rainvex
 * @Date 2022/7/29
 * @Name TxCOSUtil
 */
//腾讯云对象存储工具类
public class TxCOSUtil {

    private static final String SECRET_ID = "";
    private static final String SECRET_KEY = "";
    private static final String REGION = "ap-chengdu";

    public static COSClient initCOSClient() {
        // 1 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
        // 2 设置 bucket 的地域，clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region(REGION);
        ClientConfig clientConfig = new ClientConfig(region);
        // 建议设置使用 https 协议，从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端
        return new COSClient(cred, clientConfig);
    }
}
