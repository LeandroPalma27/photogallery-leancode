package com.leancoder.photogallery;

import com.leancoder.photogallery.models.service.JpaUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("jpaUserDetailsService")
    private JpaUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/", "index", "/css/**", "/js/**", "/img/**", "/login/**").permitAll()
        .antMatchers("/account/**").authenticated()
        .and()
        .formLogin()./* successHandler(successHandler). */loginPage("/login").permitAll();
    }

    @Autowired // Inyectamos "AuthenticationManagerBuilder"
    public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception{

        /* UserBuilder users = User.builder().passwordEncoder((password) -> passwordEncoder.encode(password)); 
        builder.inMemoryAuthentication().withUser(users.username("admin").password("1234").roles("ADMIN", "USER"));
        builder.inMemoryAuthentication().withUser(users.username("user").password("1234").roles("USER")); */

        /* builder.jdbcAuthentication().dataSource(dataSource)
        .passwordEncoder(passwordEncoder)
        .usersByUsernameQuery("select username, password, enabled from usuarios where username=?")
        .authoritiesByUsernameQuery("select u.username, a.authority from usuarios u inner join authorities a on (u.auth_id=a.id) where u.username=?");
         */

        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    
}
