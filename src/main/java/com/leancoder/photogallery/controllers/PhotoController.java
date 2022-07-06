package com.leancoder.photogallery.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.leancoder.photogallery.models.dao.IFavoritePhotoDao;
import com.leancoder.photogallery.models.dao.ILikesPhotoDao;
import com.leancoder.photogallery.models.dao.IUsuarioDao;
import com.leancoder.photogallery.models.domains.paginator.PageRenderBean;
import com.leancoder.photogallery.models.domains.validators.PhotoUpdaterValidator;
import com.leancoder.photogallery.models.domains.validators.PhotoUploaderValidator;
import com.leancoder.photogallery.models.entities.photo.FavoritePhoto;
import com.leancoder.photogallery.models.entities.photo.Photo;
import com.leancoder.photogallery.models.entities.user.User;
import com.leancoder.photogallery.models.services.photo.interfaces.IPhotoService;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
    <=== Controlador para la carga de las vistas que carguen informacion de las fotos de nuestra aplicacion.  ===>
        * Con este controlador podemos manejar la carga de vistas de informacion de una foto o lista de fotos que estan subidas
          en la base de datos.   
        * Ademas de eso tambien se gestionan los procesos y transacciones de la informacion desde el cliente hasta el servidor.
        * Tambien ser cargan objetos con informacion, de manera global.
        * Tiene como ruta el nombre "photos"
        * Actualmente carga 2 objetos en la sesion, con informacion para el procesado de el formulario que actualiza los detalles
          de una foto. El primer objeto se usa para poder obtener el id de la foto en cuestion, y el segundo objeto para mantener
          cargada informacion de los campos del formulario en caso de algun error al procesarse. 
 */
@Controller
@RequestMapping("/photos")
@SessionAttributes({ "photoDetails", "photoUpdater" })
public class PhotoController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IPhotoService photoService;

    @Autowired
    private ILikesPhotoDao likesPhotoDao;

    @Autowired
    private IFavoritePhotoDao favoritePhotoDao;

    /*
     * Objecto cargado con informacion general del usuario globalmente para todas
     * las vistas en este controlador.
     */
    @ModelAttribute("usuario")
    public User cargarUsuario(Authentication authentication, Principal principal) {
        if (principal != null) {
            return usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        } else {
            return null;
        }
    }

    /* 
     * Metodo para saber si una foto tiene un like por parte del usuario actual
     */
    public Boolean IsLiked(Long userId, Long photoId) {
        var likePhoto = likesPhotoDao.findByUserIdAndPhotoId(userId, photoId);
        var isLiked = (likePhoto != null) ? true : false;
        return isLiked;
    }

    /* 
     * Metodo para saber si una foto esta guardada por parte del usuario actual
     */
    public Boolean IsSaved(Long userId, Long photoId) {
        var favorite = favoritePhotoDao.findByUserIdAndPhotoId(userId, photoId);
        var isSaved = (favorite != null) ? true : false;
        return isSaved;
    }

    /* 
     * Metodo para fitrar fotos repetidas en una lista de fotos buscada por una keyword
     */
    public List<Photo> filterRepeatPhotos(Page<Photo> photos) {
        Set<Long> collect = photos.stream().map(i -> i.getId()).collect(Collectors.toSet());
        List<Photo> differentList = new ArrayList<Photo>();
        for(var photo : photos) {
            if(collect.contains(photo.getId())) {
                differentList.remove(photo);
                differentList.add(photo);
                continue;
            } else {
                differentList.add(photo);
            }
        }
        return differentList;
    }

    /*
     * <=== Objecto cargado con informacion(url de foto de perfil) globalmente para
     * todas las vistas en este controlador ===>
     * Se hace con el fin de poder tener la url para la carga de la foto de perfil
     * en el layout general para todas
     * las vistas.
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
     * <=== Objecto cargado con informacion(url de foto de perfil) globalmente para
     * todas las vistas en este controlador ===>
     * Se hace con el fin de poder tener la url para la carga de la foto de perfil
     * en el layout general para todas
     * las vistas.
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
     * Endpoint que carga la vista para el listado de todas las fotos registradas en
     * nuestra aplicacion.
     * Se utiliza un pagueado para la lista de fotos.
     */
    @GetMapping("/all")
    public String All(@RequestParam(name = "page", defaultValue = "0") int page, Authentication authentication,
            Model model) {

        Pageable pageRequest = PageRequest.of(page, 12);

        Page<Photo> fotos = photoService.obtenerTodasLasFotosPagueadas(pageRequest);

        PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/all", fotos);

        model.addAttribute("title", "All photos");
        model.addAttribute("photos", fotos);
        model.addAttribute("page", pageRender);

        return "photos/all";
    }

    /*
     * Endpoint que carga la vista para el listado de las fotos registradas de un
     * solo usuario, en nuestra aplicacion.
     * Se utiliza un pagueado para la lista de fotos.
     */
    @GetMapping("/own")
    public String Own(@RequestParam(name = "page", defaultValue = "0") int page, Authentication authentication,
            Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

        Pageable pageRequest = PageRequest.of(page, 12);
        Page<Photo> fotosUsuario = photoService.obtenerTodasLasFotosDeUnUsuarioPagueadas(usuario.getId(), pageRequest);
        PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/own", fotosUsuario);

        model.addAttribute("title", "My photos");
        model.addAttribute("usuario", usuario);
        model.addAttribute("photosUsuario", fotosUsuario);
        model.addAttribute("page", pageRender);

        return "photos/own";
    }

    /*
     * Endpoint que carga la vista del formulario para la subida de fotos.
     */
    @GetMapping("/upload")
    public String UploadNormalPhoto(Authentication authentication, Model model) {
        PhotoUploaderValidator validator = new PhotoUploaderValidator();
        model.addAttribute("title", "Upload photo");
        model.addAttribute("photoValidator", validator);
        return "photos/upload";
    }

    /*
     * Endpoint que procesa la subida de una foto en nuestra aplicacion.
     */
    @PostMapping("/upload")
    // No olvidar que @ModelAttribute tambien puede cargar un objeto que extrae del
    // modelo actual.
    public String uploadPhotoProccess(@Valid @ModelAttribute("photoValidator") PhotoUploaderValidator validator,
            BindingResult result, Authentication authentication, RedirectAttributes flash, SessionStatus status,
            Model model) {

        model.addAttribute("title", "Upload photo");

        if (result.hasErrors()) {

            if (validator.getFile().isEmpty()) {
                ObjectError fileError = new ObjectError("fileError", "Select some picture.");
                var errorsUpdated = new ArrayList<ObjectError>();
                result.getAllErrors().forEach((error) -> {
                    errorsUpdated.add(error);
                });
                errorsUpdated.add(fileError);
                model.addAttribute("formErrors", errorsUpdated);
                return "photos/upload";
            }

            model.addAttribute("formErrors", result.getAllErrors());
            return "photos/upload";
        }

        if (validator.getFile().isEmpty()) {
            ObjectError fileError = new ObjectError("fileError", "Select some picture.");
            var errorsUpdated = new ArrayList<ObjectError>();
            errorsUpdated.add(fileError);
            model.addAttribute("formErrors", errorsUpdated);
            return "photos/upload";
        }

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        var res = photoService.registrarFoto(validator, usuario);

        if (res.getName().equals("unknown_error")) {
            flash.addFlashAttribute("errorMessage", res.getMessage());
            return "redirect:/photos/upload";
        }

        flash.addFlashAttribute("successMessage", "Registro de foto exitoso.");
        return "redirect:/photos/own";

    }

    /*
     * Endpoint que carga los detalles de una sola foto que este subida en nuestra
     * aplicacion.
     */
    @GetMapping("/details/{public_id}")
    public String Details(@PathVariable("public_id") String public_id, Authentication authentication, Model model) {

        model.addAttribute("title", "Details");

        var photo = photoService.buscarFoto(public_id);

        if (photo == null) {
            return "redirect:/errors/not-found";
        } else {
            PhotoUpdaterValidator updater = new PhotoUpdaterValidator();
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
            var isLiked = IsLiked(usuario.getId(), photo.getId());
            var isSaved = IsSaved(usuario.getId(), photo.getId());
            updater.setTitle(photo.getTitle());
            updater.setDescription(photo.getDescription());
            model.addAttribute("photoDetails", photo);
            model.addAttribute("user", usuario);
            model.addAttribute("isLiked", isLiked);
            model.addAttribute("isSaved", isSaved);
            if (photo.getRoles().size() > 1) {
                for (var role : photo.getRoles()) {
                    if (role.getRole().equals("ROLE_PROFILE")
                            && photo.getUser().getUsername().equals(authentication.getName())) {
                        model.addAttribute("isProfilePhoto", "The current photo is set as the profile picture.");
                        break;
                    }
                }
            }
            model.addAttribute("photoUpdater", updater);
        }

        return "photos/details";
    }

    /*
     * Endpoint que actualiza los detalles de una foto que este subida en nuestra
     * aplicacion.
     */
    @PostMapping("/details/update")
    public String UpdatePhoto(@ModelAttribute("photoDetails") Photo photo,
            @Valid @ModelAttribute("photoUpdater") PhotoUpdaterValidator updater,
            BindingResult result, Authentication authentication, RedirectAttributes flash, SessionStatus status,
            Model model) {

        model.addAttribute("title", "Details");

        if (result.hasErrors()) {
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
            var isLiked = IsLiked(usuario.getId(), photo.getId());
            model.addAttribute("photoDetails", photo);
            model.addAttribute("modalActivator", "photoUpdate");
            model.addAttribute("user", usuario);
            model.addAttribute("isLiked", isLiked);
            return "photos/details";
        }

        var res = photoService.actualizarDetallesFoto(photo.getUploadId(), updater);
        if (res.getName().equals("successful_update")) {
            flash.addFlashAttribute("successMessage", res.getMessage());
        } else {
            flash.addFlashAttribute("errorMessage", res.getMessage());
        }
        status.setComplete();
        return "redirect:/photos/details/".concat(photo.getUploadId());
    }

    /*
     * Endpoint que elimina una foto que este subida en nuestra aplicacion.
     */
    @GetMapping("/details/delete/{public_id}")
    public String DeletePhoto(@PathVariable("public_id") String public_id, RedirectAttributes flash, Model model)
            throws IOException {

        var photoFound = photoService.buscarFoto(public_id);
        var res = photoService.eliminarFoto(photoFound);

        if (res.getName().equals("successful_delete")) {
            flash.addFlashAttribute("successMessage", res.getMessage());
            return "redirect:/photos/own";
        }
        flash.addFlashAttribute("errorMessage", res.getMessage());
        System.out.println(photoFound.getUploadId().toString().concat(" xdddddd"));
        return "redirect:/photos/own";

    }

    /*
     * Endpoint que cambia el estado de una foto con rol NORMAL a un rol PROFILE.
     */
    @GetMapping("/details/change-role/{public_id}")
    public String ChangeNormalPhotoToProfilePhoto(@PathVariable("public_id") String public_id,
            Authentication authentication, RedirectAttributes flash, Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        var res = photoService.establecerComoFotoDePerfil(public_id, usuario);

        if (res.getName().equals("successful_set")) {
            flash.addFlashAttribute("successMessage", res.getMessage());
            return "redirect:/account";
        } else {
            flash.addFlashAttribute("errorMessage", res.getMessage());
            return "redirect:/photos/own";
        }
    }

    /* 
     * Endpoint que carga la vista de favoritos, cargando los registros de la tabla favoritos y paginado tambien
     */
    @GetMapping("/favorites")
    public String Favorites(@RequestParam(name = "page", defaultValue = "0") int page, Authentication authentication,
            Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

        Pageable pageRequest = PageRequest.of(page, 12);
        Page<FavoritePhoto> fotos = photoService.obtenerTodosLosFavoritosPagueados(usuario.getId(), pageRequest);
        PageRenderBean<FavoritePhoto> pageRender = new PageRenderBean<FavoritePhoto>("/photos/favorites", fotos);

        model.addAttribute("title", "Favorites");
        model.addAttribute("photos", fotos);
        model.addAttribute("page", pageRender);
        return "photos/favorites";
    }

    /* 
     * Endpoint que carga la vista para la busqueda de fotos por keywords, usando paginado.
     * Se carga con la posibilidad de tener 2 parametros en la url, para asi poder cargas o nada, o un mensaje de "no se encontro nada" o las fotos encontradas
     * por ess keyword.
     */
    @GetMapping("/search")
    public String SearchPhotoByTitle(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "keyword", required = false) String keyword, Authentication authentication,
            Model model) {
        model.addAttribute("title", "Search");
        if (keyword == null) {
            Pageable pageRequest = PageRequest.of(page, 12);
            var fotos = photoService.obtenerFotosPorKeyword(keyword, pageRequest);
            PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/search", fotos);
            model.addAttribute("photos", fotos);
            model.addAttribute("page", pageRender);
        } else {
            if (keyword.trim().equals("")) {
                Pageable pageRequest = PageRequest.of(page, 12);
                var fotos = photoService.obtenerFotosPorKeyword(null, pageRequest);
                PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/search", fotos);
                model.addAttribute("emptyPhotos", "No se encontro ningun resultado.");
                model.addAttribute("photos", fotos);
                model.addAttribute("page", pageRender);
            } else {
                Pageable pageRequest = PageRequest.of(page, 12);
                var fotos = photoService.obtenerFotosPorKeyword(keyword, pageRequest);
                PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/search?keyword=".concat(keyword), fotos);
                var fotosFiltradas = filterRepeatPhotos(fotos);
                if (fotosFiltradas.isEmpty()) {
                    model.addAttribute("page", pageRender);
                    model.addAttribute("photos", fotosFiltradas);
                    model.addAttribute("emptyPhotos", "No se encontro ningun resultado.");
                } else {
                    model.addAttribute("photos", fotosFiltradas);
                    model.addAttribute("page", pageRender);
                }
            }
        }

        return "photos/search";
    }

}
