package com.leancoder.photogallery.config;

import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.leancoder.photogallery.interceptors.EnabledUserInterceptor;
import com.leancoder.photogallery.view.xml.photos.PhotosList;

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

    // Bean para el iniciador del servicio de cloudinary:
    @Bean(name = "cloudinaryInit")
    public Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap("cloud_name", "leandropalma27p", "api_key", "777658485899521",
                "api_secret", "hoOzbFZ_tZ3omPQuqQ0s7h5hJiU"));
    }

    // Bean para el uso de BCryptPasswordEncoder
    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // Interfaz que implementa la configuracion regional en una sesion de nuestra
    // aplicacion web (CARGAMOS POR DEFECTO es_ES):
    public LocaleResolver localeResolver() {
        // Almacena en sesion la configuracion regional, cada que se hace un cambio con el parametro "lang", se cambia este header.
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

    @Bean
    // Bean encargado de serializar data de java a XML.
    // Recordemos que este formato no soporta serializacion para estructura de datos como coleciones(ArrayList, Map, Set, etc).
    // Para poder hacerlo, debemos implementar una clase que envuelva a esa lista de objetos(por ejemplo una lista de clientes).
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        // Mandamos un array con todas las clases que vamos a serializar, de Object a XML:
        jaxb2Marshaller.setClassesToBeBound(new Class [] {PhotosList.class});
        return jaxb2Marshaller;
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

    // Optando por otra manera y no desde el application properties
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // TODO Auto-generated method stub
        WebMvcConfigurer.super.configureContentNegotiation(configurer);
    }

}
