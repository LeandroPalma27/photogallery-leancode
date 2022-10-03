package com.leancoder.photogallery.controllers;

import java.security.Principal;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import com.leancoder.photogallery.models.entities.user.User;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    <=== Controlador para el manejo de errores http con templates personalizados ===>
        * Con este controlador podemos manejar los errores http mas comunes como el 404 o el 500.
        * Implementa la interfaz ErrorController.
        * Actualmente este controlador soporta carga de templates personalizado para un error 404 y 500, pero podria soportar
          mas tipos de error http.      
 */
@Controller
public class CustomizeErrorController implements ErrorController {

    // Instancia para la el uso del service de la entidad usuario
    @Autowired
    private IUsuarioService usuarioService;

    /*
        <=== Objecto cargado con informacion(url de foto de perfil) globalmente para todas las vistas en este controlador ===>
     */
    @ModelAttribute("profilePictureUser")
    // Se usa la interfaz Authentication para verificar si existe un usuario logueado (con un simple authentication != null)
    public String cargarFotoDePerfil(Authentication authentication) {

        if (authentication != null) {
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

            // Del usuario encontrado, obtenemos todas sus fotos (un List de Photo intancias)
            var fotosUsuario = usuario.getPhotos();
            for (var foto : fotosUsuario) {
                // Recorremos cada foto y cuando las fotos tengan mas de 1 rol (con lo cual filtramos las fotos que no son de perfil, ya que si esta como foto de perfil deberia tener minimo 2 roles)
                if (foto.getRoles().size() > 1) {
                    // se obtendran los roles, para asi recorrerlos y verificar que tengan ROLE_PROFILE, si lo tienen se detiene el bucle ya que solo puede existir una foto de perfil por cada usuario.
                    for (var role : foto.getRoles()) {
                        if (role.getRole().equals("ROLE_PROFILE")) {
                            return foto.getUrlPhoto();
                        }
                    }
                }
            }
            // Esta funcion se repite en casi todos los controladores ya que es un dato de modelo requerido en cada ruta de la aplicacion.
        }
        // Si no hay nadie logueado, se retorna null.
        return null;

    }

    /*
        <=== Objecto cargado con informacion general del usuario globalmente para todas las vistas en este controlador ===>
     */
    @ModelAttribute("usuario")
    public User cargarUsuario(Principal principal, Authentication authentication) {
        if (principal != null) {
            return usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        } else {
            return null;
        }
    }

    /*
        Endpoint que carga las vistas personalizadas para cada tipo de error http en nuestra aplicacion.
     */
    @RequestMapping("/error")
    public String HandleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        model.addAttribute("title", "Error");

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/500";
            }
        }

        return "error/general";

    }

}
