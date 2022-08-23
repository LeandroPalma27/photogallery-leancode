package com.leancoder.photogallery.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.leancoder.photogallery.interceptors.EnabledUserInterceptor;

@Configuration
// Clase de configuracion MVC para la implementacion de interceptores HTTP
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    @Qualifier("enabledUserInterceptor")
    private EnabledUserInterceptor enabledUserInterceptor;

    // Bean para la fuente de mensajes a usar dentro de la aplicacion, buscado como "messages" y decodificado con UTF-8
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    // Añadimos el bean de la fuente de mensajes en este bean:
    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    // Interfaz que implementa la configuracion regional en una sesion de nuestra
    // aplicacion web (CARGAMOS POR DEFECTO es_ES):
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        // Por default cargara la pagina en español:
        localeResolver.setDefaultLocale(new Locale("es", "ES"));
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        // Interceptor HTTP que revisa si la url es afectada a traves una peticion GET, en
        // este caso el parametro a evaluar es "lang".
        // Ejm: ?lang=en_EN, ?lang=pt_PT, etc.
        LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
        localeInterceptor.setParamName("lang");
        return localeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Añadimos el interceptor que evalua los cambios de region en funcion al parametro lang:
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(enabledUserInterceptor)
                .addPathPatterns("/photos/own", "/photos/upload", "/photos/details/**", "/photos/favorites",
                        "/photos/search", "/account/**")
                .excludePathPatterns("/account");
    }

}
