package com.tencent.wxcloudrun.config;

import com.tencent.wxcloudrun.base.filter.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @Description :
 * @Author : yuzhuoliu
 * @Date: 2022/9/16 18:01
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private LoginFilter loginFilter;

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new TraceLogInterceptor()).addPathPatterns("/**");
//    }

    @Bean
    public FilterRegistrationBean pubFilter(){
        FilterRegistrationBean registration = new FilterRegistrationBean(loginFilter);
        registration.addUrlPatterns("/*");
        registration.setName("publishFilter");
        return registration;
    }
}
