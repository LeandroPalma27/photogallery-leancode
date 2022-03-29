package com.leancoder.photogallery;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer{
    
    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
		WebMvcConfigurer.super.addResourceHandlers(registry);

		/*String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();
		log.info(resourcePath);
		
		registry.addResourceHandler("/uploads/**")
		.addResourceLocations(resourcePath);
		*/
	}

}
