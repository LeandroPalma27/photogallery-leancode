package com.leancoder.photogallery.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

@Controller

// ESTE CONTROLADOR SE IMNPLEMENTO PARA PROBAR VISTAS SIN NUNGUN TIPO DE FUNCIONALIDAD TRANSACCIONAL (solo diseño)
@RequestMapping("/test")
public class TemplatesTestController {

    @Autowired
    IUsuarioService usuarioService;

    /*
        <=== Objecto cargado con informacion(url de foto de perfil) globalmente para todas las vistas en este controlador ===>
            * Se hace con el fin de poder tener la url para la carga de la foto de perfil en el layout general para todas
              las vistas. 
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
        <=== Objecto cargado con informacion(cloudinary_id de foto de perfil) globalmente para todas las vistas en este controlador ===>
            * Se hace con el fin de poder tener el id para la carga de la foto de perfil en el layout general para todas
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
    
    @GetMapping("/forgot-password")
    public String ForgotPasswordTemplate(Model model) {
        model.addAttribute("title", "Change password");
        return "forgot-password";
    }

}
