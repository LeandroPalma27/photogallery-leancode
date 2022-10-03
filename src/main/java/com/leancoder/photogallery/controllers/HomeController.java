package com.leancoder.photogallery.controllers;

import com.leancoder.photogallery.models.services.photo.interfaces.IPhotoService;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;


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

    // Instancias service de la entidad usuario y foto
    @Autowired
    IUsuarioService usuarioService;

    @Autowired
    IPhotoService photoService;

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
        <=== Objecto cargado con informacion(cloudinary_id de foto de perfil) globalmente para todas las vistas en este controlador ===>
            * Se hace con el fin de poder tener el id para la carga de la foto de perfil en el layout general para todas
              las vistas. 
     */
    
    /*
        Endpoint que carga la vista HOME en nuestra aplicacion.
     */
    @GetMapping({"", "/"})
    // La interfaz authentication se usara en mas de una ruta de la aplicacion (endpoint del controlador) para validar si existe un usuario logueado y asi poder trabajar con eso
    public String Home(Authentication authentication, Model model) {
        model.addAttribute("title", "Photogallery");
        if (authentication != null) {
            // Tambien en mas de una ocasion requeriremos enviar el objeto del usuario al modelo
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
            model.addAttribute("usuario", usuario);
        }

        // Sel service de fotos, enviamos las fotos con mas likes (una lista de fotos) desde nuestra base de datos (se usa una consulta sql nativa para eso)
        var fotosMasLikeadas = photoService.fotosConMasLikes();
        model.addAttribute("fotosMasLikeadas", fotosMasLikeadas);

        return "index";
    }

}
