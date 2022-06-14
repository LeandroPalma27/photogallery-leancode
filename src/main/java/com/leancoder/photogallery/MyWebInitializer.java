package com.leancoder.photogallery;

import java.util.EnumSet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;
import org.springframework.web.WebApplicationInitializer;

public class MyWebInitializer implements WebApplicationInitializer {

    // Implementamos la interfaz "WebApplicationInitializer" y sobreescribimos este
    // metodo:
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
        // la capacidad MVC de la aplicacion,
        // basada en solitudes HTTP. Dentro de este comportamiento se puede afectar a
        // las cookies, para asi gestionarlas y
        // poder protegerlas.

        // De esta manera podemos hacer que no se pueda acceder a la cookie a traves de
        // los comandos del navegador(FALSE en caso
        // contrario):
        servletContext.getSessionCookieConfig().setHttpOnly(true);

        // De esta manera podemos configurar que solo se enviara una cookie a traves de
        // una conexion HTTPS(FALSE en caso contrario):
        servletContext.getSessionCookieConfig().setSecure(false);

    }

}
