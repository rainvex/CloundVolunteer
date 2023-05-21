package com.xk.volunteer.utils;

import cn.hutool.core.util.RandomUtil;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;

import java.util.HashMap;
import java.util.Set;


/**
 * @Author Rainvex
 * @Date 2022/7/28
 * @Name TxSmsUtil
 */
//容联云短信服务工具类
public class SMSUtil {
    private static final String SERVER_IP = "";
    private static final String SERVER_PORT = "";
    private static final String ACCOUNT_SID = "";
    private static final String AUTH_TOKEN = "";
    private static final String APP_ID = "";
    private static final String TEMPLATE_ID = "1";

    public static String sendSMS(String phone) {
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(SERVER_IP, SERVER_PORT);
        sdk.setAccount(ACCOUNT_SID, AUTH_TOKEN);
        sdk.setAppId(APP_ID);
        sdk.setBodyType(BodyType.Type_JSON);
        String code = RandomUtil.randomNumbers(4);
        String[] message = { code, "5" };
        HashMap<String, Object> result = sdk.sendTemplateSMS(phone,TEMPLATE_ID,message);
        if("000000".equals(result.get("statusCode"))){
            return code;
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
            return "error";
        }
    }
}
