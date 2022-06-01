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
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    private IUsuarioService usuarioService;

    /*
        <=== Objecto cargado con informacion(url de foto de perfil) globalmente para todas las vistas en este controlador ===>
     */
    @ModelAttribute("profilePictureUser")
    public String cargarFotoDePerfil(Authentication authentication) {

        if (authentication != null) {
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

            var fotosUsuario = usuario.getPhotos();
            for (var foto : fotosUsuario) {
                if (foto.getRoles().size() > 1) {
                    for (var role : foto.getRoles()) {
                        if (role.getRole().equals("ROLE_PROFILE")) {
                            return foto.getUrlPhoto();
                        }
                    }
                }
            }
        }
        return null;

    }

    /*
        <=== Objecto cargado con informacion(url de foto de perfil) globalmente para todas las vistas en este controlador ===>
            * Se hace con el fin de poder tener la url para la carga de la foto de perfil en el layout general para todas
              las vistas. 
     */
    @ModelAttribute("profilePictureUploadId")
    public String cargarIdDeFotoPerfil(Authentication authentication) {

        if (authentication != null) {
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

            var fotosUsuario = usuario.getPhotos();
            for (var foto : fotosUsuario) {
                if (foto.getRoles().size() > 1) {
                    for (var role : foto.getRoles()) {
                        if (role.getRole().equals("ROLE_PROFILE")) {
                            return foto.getUploadId();
                        }
                    }
                }
            }
        }
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
