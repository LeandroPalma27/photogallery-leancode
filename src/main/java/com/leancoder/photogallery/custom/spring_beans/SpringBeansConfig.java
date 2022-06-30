package com.leancoder.photogallery.custom.spring_beans;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.util.EnumSet;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class SpringBeansConfig {

    @Bean(name = "cloudinaryInit")
    public Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap("cloud_name", "leandropalma27p", "api_key", "777658485899521",
                "api_secret", "hoOzbFZ_tZ3omPQuqQ0s7h5hJiU"));
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return new ServletContextInitializer() {

            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {

                // Investigar como hacer esto
                /* servletContext.getSessionCookieConfig().setName("FONKYID"); */

                // Permite indicar al servlet context que la informacion de sesion sea manejada
                // por las cookies y no por la URL(Investigar):
                servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));

                // <======= MODIFICACION DEL COMPORTAMIENTO DE LAS COOKIES A TRAVES DEL SERVLET
                // ======>
                // Con este metodo podemos modificar el comportamiento del servlet que controla
                // la capacidad MVC de la aplicacion, basada en solitudes HTTP. Dentro de este
                // comportamiento
                // se puede afectar a las cookies, para asi gestionarlas y poder protegerlas.

                // De esta manera podemos hacer que no se pueda acceder a la cookie a traves de
                // los comandos del navegador(FALSE en caso
                // contrario):
                servletContext.getSessionCookieConfig().setHttpOnly(true);

                // De esta manera podemos configurar que solo se enviara una cookie a traves de
                // una conexion HTTPS(FALSE en caso contrario):
                servletContext.getSessionCookieConfig().setSecure(false);

            }
        };

    }

}
