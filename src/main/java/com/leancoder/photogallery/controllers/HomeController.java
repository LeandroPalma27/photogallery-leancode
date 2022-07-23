package com.leancoder.photogallery.controllers;

import com.leancoder.photogallery.models.dao.IUsuarioDao;
import com.leancoder.photogallery.models.entities.user.User;
import com.leancoder.photogallery.models.services.photo.interfaces.IPhotoService;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/*
    <=== Controlador HOME de la aplicacion ===>
        * Con este controlador manejamos la vista HOME de la aplicacion.   
 */
@Controller
public class HomeController {

    @Autowired
    IUsuarioDao usuarioDao;

    @Autowired
    IUsuarioService usuarioService;

    @Autowired
    IPhotoService photoService;

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
    
    /*
        Endpoint que carga la vista HOME en nuestra aplicacion.
     */
    @GetMapping({"", "/"})
    public String Home(Authentication authentication, Model model) {
        model.addAttribute("title", "Photogallery");
        if (authentication != null) {

            var usuario = usuarioDao.findByUsername(authentication.getName());

            model.addAttribute("usuario", usuario);
        }

        var fotosMasLikeadas = photoService.fotosConMasLikes();
        model.addAttribute("fotosMasLikeadas", fotosMasLikeadas);

        return "index";
    }

}
