package com.xk.volunteer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yaml")
@ConfigurationProperties(prefix = "alipay")
@Data
public class AliPayConfig {
    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    private String returnUrl;
    private String serverUrl = "https://openapi.alipaydev.com/gateway.do";
    private String protocol = "https";
    private String signType = "RSA2";
    private String format = "json";
    private String charset = "UTF-8";
}
