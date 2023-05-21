package com.xk.volunteer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Rainvex
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  //设置映射，表示哪些请求会执行以下设置
                .allowedOriginPatterns("*")  //设置允许哪些域来访问
                .allowedHeaders("*")  //设置允许携带哪些请求头的请求进行访问
                .allowedMethods("*")  //设置允许哪些请求方式可以访问
                .maxAge(3600)  //设置生效有限期，比如这儿就是3600s内不用再次询问
                .allowCredentials(true);  //设置是否允许携带cookie
    }
}
