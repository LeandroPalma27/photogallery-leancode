package com.leancoder.photogallery;

import com.leancoder.photogallery.models.services.JpaUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Clase de configuracion de spring security en el proyecto.
@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("jpaUserDetailsService")
    private JpaUserDetailsService userDetailsService;

    // Configuracion de que rutas de la aplicacion requeriran autenticacion y cuales no.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/", "index", "/css/**", "/js/**", "/img/**", "/login/**", "/photos/all").permitAll()
        .antMatchers("/account/**", "/photos/**").authenticated()
        .and()
        .formLogin()./* successHandler(successHandler). */loginPage("/login").permitAll()
        .and()
        .logout().permitAll();
    }

    @Autowired // Inyectamos "AuthenticationManagerBuilder"
    public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception{
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    
}
