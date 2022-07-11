package com.leancoder.photogallery.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.leancoder.photogallery.interceptors.EnabledUserInterceptor;

@Configuration
public class MvcConfig implements WebMvcConfigurer{

    @Autowired
    @Qualifier("enabledUserInterceptor")
    private EnabledUserInterceptor enabledUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(enabledUserInterceptor).addPathPatterns("/photos/own", "/photos/upload", "/photos/details/**", "/photos/favorites", "/photos/search", "/account/**").excludePathPatterns("/account");
    }
    
}
