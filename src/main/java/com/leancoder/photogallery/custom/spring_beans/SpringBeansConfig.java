package com.leancoder.photogallery.custom.spring_beans;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SpringBeansConfig {
    
    @Bean(name = "cloudinaryInit")
    public Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap("cloud_name", "leandropalma27p", "api_key", "777658485899521", "api_secret", "hoOzbFZ_tZ3omPQuqQ0s7h5hJiU"));
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
