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
        http.authorizeRequests()
                .antMatchers("/css/**", "/img/**", "/photos/all", "/like", "/dislike", "/login", "/verify-account/**",
                        "/test/**", "/forgot-password", "/change-password", "/change-password/**", "/locale").permitAll()
                .antMatchers("/account/**", "/photos/**").authenticated()
                .and()

                // Permite modificar si la aplicacion necesita crear una sesion de autenticacion o si no lo
                // necesita(por defecto es IF_REQUIRED).
                // Requiere ser DESACTIVADA(stateless) SI SE USA OTRO TIPO DE SEGURIDAD DE AUTENTICACION(como JWT por ejemplo).
                // Es necesario crear una politica de sesion para autenticaciones, ya que nos permite poder manejar factores como 
                // CONCURRENCIA DE SESIONES, NOTIFICACIONES DE INICIO DE SESION DESDE OTROS LUGARES, INFORMACION DE USUARIO PARA CONTROLAR ACCESO,
                // AUTORIZACION, ROLES, ETC.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()

                // Cuando se produce la protección de fijación de sesión, se
                // SessionFixationProtectionEventpublica en el contexto de la aplicación. Si usa
                // changeSessionId, esta protección también dará como resultado
                // javax.servlet.http.HttpSessionIdListenerque se notifique cualquier correo
                // electrónico, así que tenga cuidado si su código escucha ambos eventos.
                // Consulte el capítulo Gestión de sesiones para obtener información adicional.
                // Proteccion contra ataques de fijacion de sesion (AUNQUE LA ACTIVACION DE SOLO
                // UNA SESION POR PERSONA Y EL CSRF YA NOS PROTEGEN):
                .sessionManagement().sessionFixation().changeSessionId()
                .and()

                // Carga de formulario personalizado:
                .formLogin()./* successHandler(successHandler). */loginPage("/login")
                .and()

                // Hace que al cerrar sesion, se borre la cookie de sesion que esta en el lado
                // del cliente:
                .logout(logout -> logout.deleteCookies("JSESSIONID"))
                // Proporciona soporte para el logout de nuestra aplicacion(como manipular la
                // ruta de cierre, manipular cookies, etc):
                .logout()
                .and()

                // Establece que solo se pueda tener una sola sesion valida por usuario(si el
                // usuario inicia sesion otra vez, la ultima sera invalidada, redireccionando a
                // "/login"):
                // Recordar que dentro del contexto de sesion de la aplicacion, se guardan los
                // datos de sesion de un usuario, y con eso se gestionan las sesiones
                // concurrentes:
                .sessionManagement().maximumSessions(1)

                // En caso de tener una sesion expirada o invalida, se redirige al usuario a
                // "/login":
                .expiredUrl("/login")
                .and()
                .invalidSessionUrl("/")
                .and();

                // Este comportamiento ya se hace por defecto. Basicamente copia la sesion
                // existente(si existe), si un usuario inicia
                // sesion desde otro navegador.
                /*
                 * .sessionManagement().sessionFixation().migrateSession()
                 * .and()
                 */

                /* .cors().disable(); */
        // Deshabilita la proteccion csrf(para no pedir un token de validacion que
        // verifique peticiones al servidor). Recordar que podemos usar un frontend
        // externo, y validar las peticiones a traves
        // de CSRF(SIEMPRE Y CUANDO ESE FRONTEND COMPARTA EL MISMO DOMINIO que la api).
        // .csrf().disable();

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
