package com.leancoder.photogallery.config;

import com.leancoder.photogallery.models.services.JpaUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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

    // Configuracion de que rutas de la aplicacion requeriran autenticacion y cuales
    // no.
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // El metodo authorizeRequests() permite poder restringir acceso a ciertas rutas
        // de la aplicacion,
        // usando "RequestMatchers"(obligatoriamente).
        // Por defecto, cualquier ruta existente que no este siendo tomada por este
        // metodo, no tendra restriccion alguna.
        http.authorizeRequests().antMatchers("/css/**", "/img/**", "/photos/all", "/like", "/dislike", "/login").permitAll()
                .antMatchers("/account/**", "/photos/**").authenticated()
                .and()

                // Permite modificar si la aplicacion necesita crear una sesion o si no lo necesita(por defecto es IF_REQUIRED):
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                
                // Carga de formulario personalizado:
                .formLogin()./* successHandler(successHandler). */loginPage("/login")
                .and()

                // Proporciona soporte para el logout de nuestra aplicacion(como manipular la
                // ruta de cierre, manipular cookies, etc):
                .logout()
                .and()

                // Establece que solo se pueda tener una sola sesion valida por usuario(si se
                // intenta crear == va pa "/login"):
                .sessionManagement().maximumSessions(1)

                // En caso de tener una sesion expirada o invalida, se redirige al usuario a
                // "/login":
                .expiredUrl("/login")
                .and().invalidSessionUrl("/login")
                .and()

                // Este comportamiento ya se hace por defecto. Basicamente copia la sesion
                // existente(si existe), si un usuario inicia
                // sesion desde otro navegador.
                /* .sessionManagement().sessionFixation().migrateSession()
                .and() */

                .cors().disable()

        // Deshabilita la proteccion csrf(para no pedir un token de validacion que
        // verifique peticiones al servidor)
        /* .csrf().disable(); */
        ;
    }

    @Autowired // Inyectamos "AuthenticationManagerBuilder"
    public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
