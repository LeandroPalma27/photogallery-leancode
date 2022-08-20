package com.leancoder.photogallery.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.leancoder.photogallery.models.dao.IFavoritePhotoDao;
import com.leancoder.photogallery.models.dao.ILikesPhotoDao;
import com.leancoder.photogallery.models.domains.paginator.PageRenderBean;
import com.leancoder.photogallery.models.domains.validators.PhotoUpdaterValidator;
import com.leancoder.photogallery.models.domains.validators.PhotoUploaderValidator;
import com.leancoder.photogallery.models.entities.photo.FavoritePhoto;
import com.leancoder.photogallery.models.entities.photo.Photo;
import com.leancoder.photogallery.models.entities.user.User;
import com.leancoder.photogallery.models.services.photo.interfaces.IPhotoService;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // Inyecte acceso al dao de las entidades likePhoto y favoritePhoto para ahorrar lineas de codigo en implentaciones innecesarias
    @Autowired
    private ILikesPhotoDao likesPhotoDao;

    // Inyectamos para cargar desde nuestra fuente de traducciones(messages_en/es/us/pt/ko.properties)
    @Autowired
    private IFavoritePhotoDao favoritePhotoDao;

    @Autowired
    private MessageSource messageSource;

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
     * Metodo para fitrar fotos repetidas en una lista de fotos buscada por una
     * keyword
     */
    public List<Photo> filterRepeatPhotos(Page<Photo> photos) {
        Set<Long> collect = photos.stream().map(i -> i.getId()).collect(Collectors.toSet());
        List<Photo> differentList = new ArrayList<Photo>();
        for (var photo : photos) {
            if (collect.contains(photo.getId())) {
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
     * Endpoint que carga la vista para el listado de todas las fotos registradas en
     * nuestra aplicacion.
     * Se utiliza un pagueado para la lista de fotos.
     */
    @GetMapping("/all")
    public String All(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "filter", required = false) String sort2,
            @RequestParam(name = "order", required = false) String sort1, Authentication authentication,
            Model model) {

        // Este endpoint puede recibir 3 tipos de parametros en la url, aunque los 3 son no requeridos puesto que el de paginado tiene un valor por defecto.
        model.addAttribute("title", "Todas las fotos");

        Pageable pageRequest = PageRequest.of(page, 12);

        // Las fotos pueden ser pagueadas en funcion un tipo de orden, por defecto se paguean desde las mas antiguas a las mas recientes
        Page<Photo> fotos = photoService.obtenerTodasLasFotosPagueadas(pageRequest, sort1);

        // En funcion cuales y cuantos sort esten llenos, se cargara una url personalizada para el pageRender
        // Ningun sort
        if (sort1 == null && sort2 == null) {
            PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/all", fotos);
            model.addAttribute("photos", fotos);
            model.addAttribute("page", pageRender);
        }

        // Solo el sort2
        if (sort1 == null && sort2 != null) {
            PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/all", fotos);
            model.addAttribute("photos", fotos);
            model.addAttribute("page", pageRender);
        }

        // Solo sort1
        if (sort1 != null && sort2 == null) {
            PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/all?order=".concat(sort1), fotos);
            model.addAttribute("photos", fotos);
            model.addAttribute("page", pageRender);
        }

        // Los dos sort
        if (sort1 != null && sort2 != null) {
            PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/all", fotos);
            model.addAttribute("photos", fotos);
            model.addAttribute("page", pageRender);
        }

        return "photos/all";

    }

    /*
     * Endpoint que carga la vista para el listado de las fotos registradas de un
     * solo usuario, en nuestra aplicacion.
     * Se utiliza un pagueado para la lista de fotos.
     */
    @GetMapping("/own")
    public String Own(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "filter", required = false) String sort2,
            @RequestParam(name = "order", required = false) String sort1, Authentication authentication,
            Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

        // Aca se repite el mismo proceso, con la diferencia de que solo se cargaran las fotos de un usuario
        Pageable pageRequest = PageRequest.of(page, 12);
        Page<Photo> fotosUsuario = photoService.obtenerTodasLasFotosDeUnUsuarioPagueadas(usuario.getId(), pageRequest, sort1);

        if (sort1 == null && sort2 == null) {
            PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/all", fotosUsuario);
            model.addAttribute("photosUsuario", fotosUsuario);
            model.addAttribute("page", pageRender);
        }

        if (sort1 == null && sort2 != null) {
            PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/all", fotosUsuario);
            model.addAttribute("photosUsuario", fotosUsuario);
            model.addAttribute("page", pageRender);
        }

        if (sort1 != null && sort2 == null) {
            PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/all?order=".concat(sort1), fotosUsuario);
            model.addAttribute("photosUsuario", fotosUsuario);
            model.addAttribute("page", pageRender);
        }

        if (sort1 != null && sort2 != null) {
            PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/all", fotosUsuario);
            model.addAttribute("photosUsuario", fotosUsuario);
            model.addAttribute("page", pageRender);
        }

        model.addAttribute("title", "Mis fotos");
        model.addAttribute("usuario", usuario);

        return "photos/own";
    }

    /*Usuario
     * Endpoint que carga la vista del formulario para la subida de fotos.
     */
    // Inyectamos Locale para el uso de traduccion en texto pasado desde un endpoint de un controlador:
    @GetMapping("/upload")
    public String UploadNormalPhoto(Authentication authentication, Model model, Locale locale) {
        PhotoUploaderValidator validator = new PhotoUploaderValidator();
        // Y en lugar de pasar el texto de manera literal, pasamos el objecto messsageSource(CARGA LA FUENTE DE NUESTRO ARCHIVO DE TRADUCCIONES), y como parametros colocamos la llave del texto que queremos cargar
        // , un null y tambien el objeto locale.
        model.addAttribute("title", messageSource.getMessage("text.photos.upload.title", null, locale));
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
            BindingResult result, Authentication authentication, RedirectAttributes flash,
            Model model) {

        model.addAttribute("title", "Publicar foto");

        if (result.hasErrors()) {

            // Si el objeto multipartFile del usuario no se llena, cargo un error ya que no hay manera de validar archivos en formularios a traves de anotaciones
            if (validator.getFile().isEmpty()) {
                ObjectError fileError = new ObjectError("fileError", "Selecciona una archivo.");
                var errorsUpdated = new ArrayList<ObjectError>();
                result.getAllErrors().forEach((error) -> {
                    errorsUpdated.add(error);
                });
                errorsUpdated.add(fileError);
                model.addAttribute("formErrors", errorsUpdated);
                return "photos/upload";
            }

            // Caso contrario solo cargan los errores previamente generados
            model.addAttribute("formErrors", result.getAllErrors());
            return "photos/upload";
        }

        // Si solo el archivo esta vacio, necesitamos cargar el error de manera manual
        if (validator.getFile().isEmpty()) {
            ObjectError fileError = new ObjectError("fileError", "Selecciona un archivo.");
            var errorsUpdated = new ArrayList<ObjectError>();
            errorsUpdated.add(fileError);
            model.addAttribute("formErrors", errorsUpdated);
            return "photos/upload";
        }

        // Si no hay nigun error, se procesa la foto a traves del service
        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        var res = photoService.registrarFoto(validator, usuario);

        // Y en caso de error en el proceso, se cargara el respectivo mensaje y se hara una redireccion a la vista del formulario
        if (res.getName().equals("unknown_error")) {
            flash.addFlashAttribute("errorMessage", res.getMessage());
            return "redirect:/photos/upload";
        }

        flash.addFlashAttribute("successMessage", "Registro de foto exitoso.");
        return "redirect:/photos/details/".concat(res.getMessage());

    }

    /*
     * Endpoint que carga los detalles de una sola foto que este subida en nuestra
     * aplicacion.
     */
    @GetMapping("/details/{public_id}")
    // La foto se obtiene a traves del public_id, cargado en la ruta del endpoint
    public String Details(@PathVariable("public_id") String public_id, Authentication authentication, Model model) {

        model.addAttribute("title", "Detalles");

        // Buscamos la foto
        var photo = photoService.buscarFoto(public_id);

        // Si no se encuentra la foto, cargamos una vista de error 404
        if (photo == null) {
            return "redirect:/errors/not-found";
        // Caso contrario, cargaremos los datos necesarios al modelo
        } else {
            PhotoUpdaterValidator updater = new PhotoUpdaterValidator();
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
            // Se carga informacion sobre la foto en cuestion, para saber tiene un like por parte del usuario y tambien si esta guardada en favoritos
            var isLiked = IsLiked(usuario.getId(), photo.getId());
            var isSaved = IsSaved(usuario.getId(), photo.getId());
            updater.setTitle(photo.getTitle());
            updater.setDescription(photo.getDescription());
            model.addAttribute("photoDetails", photo);
            model.addAttribute("user", usuario);
            model.addAttribute("isLiked", isLiked);
            model.addAttribute("isSaved", isSaved);
            // Analizamos si la foto cargada tiene rol de foto de perifl, para asi cargar un dato opcional al modelo
            if (photo.getRoles().size() > 1) {
                for (var role : photo.getRoles()) {
                    if (role.getRole().equals("ROLE_PROFILE")
                            && photo.getUser().getUsername().equals(authentication.getName())) {
                        model.addAttribute("isProfilePhoto",
                                "Actualmente esta foto esta seleccionada como foto de perfil.");
                        break;
                    }
                }
            }
            // Y el objeto requerido para el formulario
            model.addAttribute("photoUpdater", updater);
        }

        return "photos/details";
    }

    /*
     * Endpoint que actualiza los detalles de una foto que este subida en nuestra
     * aplicacion.
     */
    // Esta ruta procesara el formulario a traves de un post request
    @PostMapping("/details/update")
    public String UpdatePhoto(@ModelAttribute("photoDetails") Photo photo,
            @Valid @ModelAttribute("photoUpdater") PhotoUpdaterValidator updater,
            BindingResult result, Authentication authentication, RedirectAttributes flash, SessionStatus status,
            Model model) {

        model.addAttribute("title", "Detalles");

        if (result.hasErrors()) {
            // En caso de error, volvemos a cargar informacion a la vista ya que no se redirecciona, solo se carga la misma peticion post pero con la vista incluida
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
            var isLiked = IsLiked(usuario.getId(), photo.getId());
            model.addAttribute("photoDetails", photo);
            model.addAttribute("modalActivator", "photoUpdate");
            model.addAttribute("user", usuario);
            model.addAttribute("isLiked", isLiked);
            return "photos/details";
        }

        // Caso contrario, se procesa y se carga el mensaje mas la redireccion, respectivamente
        var res = photoService.actualizarDetallesFoto(photo.getUploadId(), updater);
        if (res.getName().equals("successful_update")) {
            flash.addFlashAttribute("successMessage", res.getMessage());
        } else {
            flash.addFlashAttribute("errorMessage", res.getMessage());
        }
        // ESTE FORMULARIO TRABAJACON OBJETOS GUARDADOS EN SESION, SIENDO EL OBJETO PHOTO Y EL DEL FORMULARIO DE ACTUALIZACION DE FOTO, al completarse con exito es necesario resetear la sesion.
        status.setComplete();
        return "redirect:/photos/details/".concat(photo.getUploadId());
    }

    /*
     * Endpoint que elimina una foto que este subida en nuestra aplicacion.
     */
    @GetMapping("/details/delete/{public_id}")
    // Requiere el public_id de la foto
    public String DeletePhoto(@PathVariable("public_id") String public_id, RedirectAttributes flash, Model model)
            throws IOException {

        // Se busca la foto para poder eliminarla desde el dao, a traves del service de foto
        var photoFound = photoService.buscarFoto(public_id);
        var res = photoService.eliminarFoto(photoFound);

        // Se procesa y se carga el mensaje mas la redireccion respectiva
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
    // Sirve para seleccionar una foto con rol normal, como una foto de perfil
    public String ChangeNormalPhotoToProfilePhoto(@PathVariable("public_id") String public_id,
            Authentication authentication, RedirectAttributes flash, Model model) {

        // Necesitamos el usuario y la foto para poder crear un registro de ROLE_PROFILE para esa foto (no debe tener el rol previo), borrando el role profile existente por parte del usuario en caso de que exista
        // De todas las fotos de un usuario, solo una tendra rol de perfil
        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        var res = photoService.establecerComoFotoDePerfil(public_id, usuario);

        // Cargamos flash y redireccionamos respectivamente
        if (res.getName().equals("successful_set")) {
            flash.addFlashAttribute("successMessage", res.getMessage());
            return "redirect:/account";
        } else {
            flash.addFlashAttribute("errorMessage", res.getMessage());
            return "redirect:/photos/own";
        }
    }

    /*
     * Endpoint que carga la vista de favoritos, cargando los registros de la tabla
     * favoritos y paginado tambien
     */
    @GetMapping("/favorites")
    public String Favorites(@RequestParam(name = "page", defaultValue = "0") int page, Authentication authentication,
            Model model) {

        // Se requiere el usuario para obtener sus fotos guardadas en favoritos
        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

        // Y tambien conlleva un pagueo
        Pageable pageRequest = PageRequest.of(page, 12);
        Page<FavoritePhoto> fotos = photoService.obtenerTodosLosFavoritosPagueados(usuario.getId(), pageRequest);
        PageRenderBean<FavoritePhoto> pageRender = new PageRenderBean<FavoritePhoto>("/photos/favorites", fotos);

        model.addAttribute("title", "Favoritos");
        model.addAttribute("photos", fotos);
        model.addAttribute("page", pageRender);
        return "photos/favorites";
    }

    /*
     * Endpoint que carga la vista para la busqueda de fotos por keywords, usando
     * paginado.
     * Se carga con la posibilidad de tener 2 parametros en la url, para asi poder
     * cargas o nada, o un mensaje de "no se encontro nada" o las fotos encontradas
     * por ess keyword.
     */
    @GetMapping("/search")
    // En la url se puede incluir un parametro que se tomara como keyword para la busqueda de algun registro, pero no es obligatorio llevarlo
    public String SearchPhotoByTitle(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "keyword", required = false) String keyword, Authentication authentication,
            Model model) {
        model.addAttribute("title", "Buscar");
        if (keyword == null) {
            Pageable pageRequest = PageRequest.of(page, 12);
            var fotos = photoService.obtenerFotosPorKeyword(null, pageRequest);
            PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/search", fotos);
            // Lo que se carga es null
            model.addAttribute("photos", fotos);
            model.addAttribute("page", pageRender);
        } else {
            // De esta manera filtro que se hagan busquedas con cadenas vacias o con cadenas de solo una letra
            if (keyword.trim().equals("") || keyword.trim().length() == 1) {
                Pageable pageRequest = PageRequest.of(page, 12);
                var fotos = photoService.obtenerFotosPorKeyword(null, pageRequest);
                PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/search", fotos);
                model.addAttribute("emptyPhotos", "No se encontro ningun resultado.");
                model.addAttribute("photos", fotos);
                model.addAttribute("page", pageRender);
            } else {
                // Caso contrario, se hara la busqueda
                Pageable pageRequest = PageRequest.of(page, 12);
                var fotos = photoService.obtenerFotosPorKeyword(keyword, pageRequest);
                PageRenderBean<Photo> pageRender = new PageRenderBean<Photo>("/photos/search?keyword=".concat(keyword),
                        fotos);
                // Filtramos las fotos repetidas (porque puede que existan registros repetidos en la lista de fotos) 
                var fotosFiltradas = filterRepeatPhotos(fotos);
                // En caso de no haberse encontrado ninguna foto con la respectiva keyword:
                if (fotosFiltradas.isEmpty()) {
                    model.addAttribute("page", pageRender);
                    model.addAttribute("photos", fotosFiltradas);
                    model.addAttribute("emptyPhotos", "No se encontro ningun resultado.");
                // Caso contrario, se cargan las fotos mas el pageRender
                } else {
                    model.addAttribute("photos", fotosFiltradas);
                    model.addAttribute("page", pageRender);
                }
            }
        }

        return "photos/search";
    }

}
