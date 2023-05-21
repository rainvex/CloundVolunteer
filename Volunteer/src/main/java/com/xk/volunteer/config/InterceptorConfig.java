package com.xk.volunteer.config;

import com.xk.volunteer.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor = new JwtInterceptor();
    @Bean
    JwtInterceptor jwtInterceptor() {
        return jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/base/index","/base/getVerifyCode","/base/login","/base/register","/base/image/upload","/base/image/remove")
                .excludePathPatterns("/volunteer/queryByCondition")
                .excludePathPatterns("/team/queryByCondition")
                .excludePathPatterns("/unit/queryByCondition")
                .excludePathPatterns("/echarts/epidemic")
                .excludePathPatterns("/activity/queryByCondition","/activity/getActivityById")
                .excludePathPatterns("/inform/getAllInform","/inform/getInform","/inform/getInformHighView")
                .excludePathPatterns("/donate/donateCallback");
    }
}
