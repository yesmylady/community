package com.example.community.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 这里复制官方代码，去掉了影响本地css的@EnableWebMvc注解，
 * override添加拦截器方法，并把本地的拦截器加进去(拦截器必须实现HandlerInterceptor接口)
 * 拦截器中最重要的是preHandle方法，返回boolean决定是否放行这个request
 */

@Configuration
//@EnableWebMvc  // 手动接管spring mvc，一般不用
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LocaleInterceptor());
//        // 两个函数表示：哪些地址拦截，哪些地址不拦截
//        registry.addInterceptor(new ThemeInterceptor()).addPathPatterns("/**").excludePathPatterns("/admin/**");
//        registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/secure/*");
        registry.addInterceptor(sessionInterceptor).addPathPatterns("/**").excludePathPatterns("/css/**");
    }
}