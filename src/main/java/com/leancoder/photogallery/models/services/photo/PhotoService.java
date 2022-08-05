package com.leancoder.photogallery.models.services.photo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.leancoder.photogallery.custom.rest_provider.cloudinary.interfaces.ICloudinaryProvider;
import com.leancoder.photogallery.models.dao.IFavoritePhotoDao;
import com.leancoder.photogallery.models.dao.ILikesPhotoDao;
import com.leancoder.photogallery.models.dao.IPhotoDao;
import com.leancoder.photogallery.models.dao.IRolePhotoDao;
import com.leancoder.photogallery.models.dao.IUsuarioDao;
import com.leancoder.photogallery.models.domains.functionalities.current_date.CurrentDateBean;
import com.leancoder.photogallery.models.domains.responses.FotosConMasLikes;
import com.leancoder.photogallery.models.domains.responses.RestRequestResponse;
import com.leancoder.photogallery.models.domains.responses.UpdateOrRegisterDetailsResponse;
import com.leancoder.photogallery.models.domains.validators.PhotoUpdaterValidator;
import com.leancoder.photogallery.models.domains.validators.PhotoUploaderValidator;
import com.leancoder.photogallery.models.entities.photo.FavoritePhoto;
import com.leancoder.photogallery.models.entities.photo.LikesPhoto;
import com.leancoder.photogallery.models.entities.photo.Photo;
import com.leancoder.photogallery.models.entities.photo.RolePhoto;
import com.leancoder.photogallery.models.entities.user.User;
import com.leancoder.photogallery.models.services.photo.interfaces.IPhotoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/* 
    * Clase service que implementa toda la logica de negocio para las consultas y transacciones de las fotos registradas en la
      aplicacion.
 */
@Service
public class PhotoService implements IPhotoService {

    @Autowired
    private IPhotoDao photoDao;

    @Autowired
    private IUsuarioDao usuarioDao;

    @Autowired
    private IRolePhotoDao rolePhotoDao;

    @Autowired
    private ILikesPhotoDao likesPhotoDao;

    @Autowired
    private IFavoritePhotoDao favoritePhotoDao;

    @Autowired
    private ICloudinaryProvider cloudinaryService;

    @Autowired
    JdbcTemplate jdbc;

    // Metodo para subir una foto al servidor (si procede con exito, se retorna un objeto Photo con algunos datos):
    private Photo subirFotoAlServidor(PhotoUploaderValidator validator) {

        CurrentDateBean currentDate = new CurrentDateBean("yyyy-MM-dd HH:mm:ss");
        var photoUploaded = cloudinaryService.upload(validator.getFile());

        // Si procede:
        if ((boolean) photoUploaded.get("isUpload")) {

            Photo photo = new Photo();
            photo.setUploadId(photoUploaded.get("id").toString());
            photo.setLikes((long) 0);
            photo.setTitle(validator.getTitle());
            photo.setDescription(validator.getDescription());
            photo.setUrlPhoto(photoUploaded.get("url").toString());
            photo.setDate(currentDate.getCurrentDate());

            return photo;

        }
        // Caso contrario
        return null;
    }

    // metodo que registra una foto en la base de datos (PRIMERO LA FOTO DEBERIA ESTAR SUBIDA EN CLOUDINARY)
    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse registrarFoto(PhotoUploaderValidator photoValidator, User usuario) {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();
        // Subimos a cloudinary y esperamos a que se retorne el objeto Photo para completar algunos campos:
        var photo = subirFotoAlServidor(photoValidator);

        // Se llenan los campos faltantes:
        if (photo != null) {
            photo.setUser(usuario);
            photo.setRoles(Arrays.asList(new RolePhoto("ROLE_NORMAL", photo)));
            photoDao.save(photo);
            response.setName("successful");
            response.setMessage(photo.getUploadId());
            // Se retorna el objeto response para redireccionar correspondientemente:
            return response;
        }
        // Si hubo un fallo y la foto no se subio a cloudinary:
        response.setName("unknown_error");
        response.setMessage("Ocurrio un error inesperado, intentelo de nuevo.");
        return response;

    }

    // Busca foto por el cloudinary id:
    @Override
    @Transactional(readOnly = true)
    public Photo buscarFoto(String public_id) {
        return photoDao.findByUpload_id(public_id);
    }

    // Obtiene todas las fotos:
    @Override
    @Transactional(readOnly = true)
    public Iterable<Photo> obtenerTodasLasFotos() {
        return photoDao.findAll();
    }

    // Actualiza solo titulo y descripcion de la foto.
    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse actualizarDetallesFoto(String uploadId,
            PhotoUpdaterValidator photoValidator) {
        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();

        // Busca la foto por el cloudinary id
        var photoFound = buscarFoto(uploadId);

        if (photoFound != null) {
            photoFound.setTitle(photoValidator.getTitle());
            photoFound.setDescription(photoValidator.getDescription());

            photoDao.save(photoFound);

            // Rellena el response correspondiente, si se procesa con exito
            response.setName("successful_update");
            response.setMessage("Se actualizo los detalles.");
            return response;
        }

        // o si se procesa de manera fallida:
        response.setName("error_update");
        response.setMessage("Ocurrio un error, intentelo mas tarde.");
        return response;
    }

    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse eliminarFoto(Photo photo) throws IOException {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();

        // Eliminamos la foto con el cloudinary id, a traves del service:
        var res = cloudinaryService.delete(photo.getUploadId());

        // Y segun el response:
        if ((boolean) res.get("isDelete")) {
            likesPhotoDao.deleteByPhoto_Id(photo.getId());
            favoritePhotoDao.deleteByPhoto_Id(photo.getId());
            photoDao.deleteById2(photo.getId());
            // Si se procesa con exito:
            response.setName("successful_delete");
            response.setMessage("La foto se elimino con exito.");
            return response;
        }

        // Si se procesa sin exito:
        response.setName("error_delete");
        response.setMessage("Ocurrio algun error al borrar la foto, intentelo mas tarde.");
        return response;
    }

    // Se utiliza para paguear la lista de todas las fotos (pero con un sort, en este caso un sort que ordena de manera ascendente o descendente).
    @Override
    @Transactional(readOnly = true)
    public Page<Photo> obtenerTodasLasFotosPagueadas(Pageable pageable, String sort1) {
        if (sort1 == null) {
            // Si no se incluye sort:
            return photoDao.findAll(pageable);
        }
        // Si se incluye:
        if (sort1.equals("likesCountASC")) {
            return photoDao.findAllOrderByLikesCountAsc(pageable);
        } else if (sort1.equals("likesCountDESC")) {
            return photoDao.findAllOrderByLikesCountDesc(pageable);
        } else if (sort1.equals("dateASC")) {
            return photoDao.findAllOrderByDateAsc(pageable);
        } else if (sort1.equals("dateDESC")) {
            return photoDao.findAllOrderByDateDesc(pageable);
        }
        // Si el sort es una cadena vacia o no coincide con los ordenamientos disponibles (likesCountASC, likesCountDESC, dateASC, dateDESC):
        return photoDao.findAll(pageable);

    }

    // Se utiliza para paguear la lista de todas las fotos de un solo usuario.
    @Override
    @Transactional(readOnly = true)
    public Page<Photo> obtenerTodasLasFotosDeUnUsuarioPagueadas(Long user_id, Pageable pageable, String sort1) {
        if (sort1 == null) {
            return photoDao.findByUser_id(user_id, pageable);
        }
        if (sort1.equals("likesCountASC")) {
            return photoDao.findAllOrderByLikesCountAscAndUser_Id(user_id, pageable);
        } else if (sort1.equals("likesCountDESC")) {
            return photoDao.findAllOrderByLikesCountDescAndUser_Id(user_id, pageable);
        } else if (sort1.equals("dateASC")) {
            return photoDao.findAllOrderByDateAscAndUser_Id(user_id, pageable);
        } else if (sort1.equals("dateDESC")) {
            return photoDao.findAllOrderByDateDescAndUser_Id(user_id, pageable);
        }
        return photoDao.findByUser_id(user_id, pageable);
    }

    // El formulario que procesa el registro esta en la vista del perfil del
    // usuario.
    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse registrarFotoPerfil(PhotoUploaderValidator photoValidator, User usuario) {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();
        var photo = subirFotoAlServidor(photoValidator);

        if (photo != null) {

            var fotosUsuario = photoDao.findByUser_id(usuario.getId());
            // Verificamos si el usuario tiene una foto de perfil:
            principal: for (var foto : fotosUsuario) {
                if (foto.getRoles().size() > 1) {
                    for (var role : foto.getRoles()) {
                        if (role.getRole().equals("ROLE_PROFILE")) {
                            rolePhotoDao.deleteById2(role.getId());
                            break principal;
                        }
                    }
                } else {
                    // Vamos saltando de foto en foto hasta encontrar, si no existe una foto de perfil actual se procesa todo con normalidad:
                    continue;
                }
            }
            photo.setUser(usuario);
            photo.setRoles(Arrays.asList(new RolePhoto("ROLE_NORMAL", photo), new RolePhoto("ROLE_PROFILE", photo)));
            photoDao.save(photo);
            response.setName("successful");
            return response;

        }

        response.setName("unknown_error");
        response.setMessage("Ocurrio un error inesperado, intentelo de nuevo.");
        return response;
    }

    // Se puede remover desde el perfil del usuario o desde la lista de fotos del
    // usuario
    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse removerFotoDePerfil(String uploadId) {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();

        var foto = photoDao.findByUpload_id(uploadId);
        var rolesDeLaFotoDePerfilActual = rolePhotoDao.findyByPhoto_id(foto.getId());

        if (foto != null) {
            // Recorremos todos los roles disponibles de la foto de perfil a eliminar, para borrar el ROLE_PROFILE:
            for (var rol : rolesDeLaFotoDePerfilActual) {
                if (rol.getRole().equals("ROLE_PROFILE")) {
                    rolePhotoDao.deleteById2(rol.getId());
                    // Si se procesa con exito:
                    response.setName("successful_remove");
                    response.setMessage("La foto se quito con exito.");
                    return response;
                } else {
                    // Seguimos buscando rol por rol hasta encontrar el ROLE_PROFILE:
                    continue;
                }
            }
        }

        // Si la foto no existe:
        response.setName("photo_notFound");
        response.setMessage("La foto no existe.");
        return response;

    }

    // Solo se hace desde la lista de fotos de un usuario.
    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse establecerComoFotoDePerfil(String uploadId, User usuario) {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();

        // Buscamos foto actual para añadirle el rol profile:
        var fotoActual = photoDao.findByUpload_id(uploadId);
        // Buscamos todas las fotos del usuario para analizar los roles de cada una de sus fotos, y en caso de que tenga ROLE_PROFILE, se elimina ese rol:
        var fotosUsuario = photoDao.findByUser_id(usuario.getId());

        if (fotoActual != null) {

            // Recorremos foto por foto, analizando los roles de cada foto para encontrar un posible ROLE_PROFILE:
            principal: for (var foto : fotosUsuario) {
                if (foto.getRoles().size() > 1) {
                    for (var role : foto.getRoles()) {
                        if (role.getRole().equals("ROLE_PROFILE")) {
                            rolePhotoDao.deleteById2(role.getId());
                            break principal;
                        } else {
                            continue;
                        }
                    }
                } else {
                    continue;
                }
            }

            // Creamos un rol para la foto seleccionada:
            RolePhoto role = new RolePhoto();
            role.setPhoto(fotoActual);
            role.setRole("ROLE_PROFILE");

            // De los roles de la foto seleccionada, los obtenemos en una lista y añadimos el rol creado anteriormente:
            var rolesFotoActual = fotoActual.getRoles();
            rolesFotoActual.add(role);

            // Actualizamos los roles:
            fotoActual.setRoles(rolesFotoActual);
            // Guardamos la foto:
            photoDao.save(fotoActual);

            response.setName("successful_set");
            response.setMessage("Se cambio la foto de perfil.");
            return response;

        } else {
            response.setName("photo_notFound");
            response.setMessage("No se encontro la foto.");
            return response;
        }

    }

    // Metodo para transaccionar un like en una foto, por parte de un usuario.
    @Override
    @Transactional
    public RestRequestResponse likearFoto(String photo_id, String user) {

        RestRequestResponse response = new RestRequestResponse();
        CurrentDateBean currentDate = new CurrentDateBean("yyyy-MM-dd HH:mm:ss");

        var photo = photoDao.findByUpload_id(photo_id);
        var usuario = usuarioDao.findByUsername(user);

        if (photo == null) {
            response.setIsSuccesful(false);
            response.setMessage("photo_notFound");
            response.setDescription("La foto no existe en el sistema.");
            return response;
        }

        if (usuario == null) {
            response.setIsSuccesful(false);
            response.setMessage("user_notFound");
            response.setDescription("El usuario indicado no existe en el sistema.");
            return response;
        }

        if (likesPhotoDao.findByUserIdAndPhotoId(usuario.getId(), photo.getId()) != null) {
            response.setIsSuccesful(false);
            response.setMessage("like_exists");
            response.setDescription("Ya existe un like del usuario ".concat(usuario.getUsername()));
            return response;
        }

        // Si todo se procesa, creamos un registro de like:
        LikesPhoto like = new LikesPhoto();
        like.setPhoto(photo);
        like.setUser(usuario);
        like.setDate(currentDate.getCurrentDate());
        long nuevaCantidadLikes = photo.getLikes();
        // Añadimos un digito al contador de likes de la foto:
        photo.setLikes(++nuevaCantidadLikes);
        // Guardamos la foto:
        photoDao.save(photo);
        likesPhotoDao.save(like);

        response.setIsSuccesful(true);
        response.setMessage("like_success");
        response.setDescription("El registro del like fue exitoso.");

        return response;
    }

    // Metodo para transaccionar un dislike en una foto, por parte de un usuario.
    @Override
    @Transactional
    public RestRequestResponse quitarLike(String photo_id, String user) {
        RestRequestResponse response = new RestRequestResponse();

        var photo = photoDao.findByUpload_id(photo_id);
        var usuario = usuarioDao.findByUsername(user);

        if (photo == null) {
            response.setIsSuccesful(false);
            response.setMessage("photo_notFound");
            response.setDescription("La foto no existe en el sistema.");
            return response;
        }

        if (usuario == null) {
            response.setIsSuccesful(false);
            response.setMessage("user_notFound");
            response.setDescription("El usuario indicado no existe en el sistema.");
            return response;
        }

        // Buscamos el registro de like:
        var like = likesPhotoDao.findByUserIdAndPhotoId(usuario.getId(), photo.getId());

        if (like == null) {
            response.setIsSuccesful(false);
            response.setMessage("like_notExists");
            response.setDescription("No existe un like del usuario ".concat(usuario.getUsername()));
            return response;
        }

        // Lo borramos de la tabla:
        likesPhotoDao.delete2(like.getId());
        long nuevaCantidadLikes = photo.getLikes();
        // Quitamos un digito a la cantidad de likes de la columna numerica que tiene el contador de likes, en un registro de una foto:
        photo.setLikes(--nuevaCantidadLikes);
        // Guardamos la foto con el like descontado:
        photoDao.save(photo);

        response.setIsSuccesful(true);
        response.setMessage("dislike_success");
        response.setDescription("El dislike fue exitoso.");

        return response;
    }

    // Metodo para transaccionar una foto a favoritos, por parte de un usuario.
    @Override
    @Transactional
    public RestRequestResponse añadirFavoritos(String photo_id, String user) {
        RestRequestResponse response = new RestRequestResponse();
        CurrentDateBean currentDate = new CurrentDateBean("yyyy-MM-dd HH:mm:ss");

        var photo = photoDao.findByUpload_id(photo_id);
        var usuario = usuarioDao.findByUsername(user);

        if (photo == null) {
            response.setIsSuccesful(false);
            response.setMessage("photo_notFound");
            response.setDescription("La foto no existe en el sistema.");
            return response;
        }

        if (usuario == null) {
            response.setIsSuccesful(false);
            response.setMessage("user_notFound");
            response.setDescription("El usuario indicado no existe en el sistema.");
            return response;
        }

        // Se crea un registro de un favorito, con el id del usuario que añadio esa foto a sus favoritos y con el id de la foto a añadir:
        FavoritePhoto favorite = new FavoritePhoto();
        favorite.setDate(currentDate.getCurrentDate());
        favorite.setPhoto(photo);
        favorite.setUser(usuario);

        favoritePhotoDao.save(favorite);
        response.setIsSuccesful(true);
        response.setMessage("favorite_success");
        response.setDescription("La foto fue guardada en favoritos satisfactoriamente.");

        return response;
    }

    // Metodo para transaccionar el quite de una foto de favoritos, por parte de un
    // usuario.
    @Override
    @Transactional
    public RestRequestResponse quitarFavoritos(String photo_id, String user) {
        RestRequestResponse response = new RestRequestResponse();

        var photo = photoDao.findByUpload_id(photo_id);
        var usuario = usuarioDao.findByUsername(user);

        if (photo == null) {
            response.setIsSuccesful(false);
            response.setMessage("photo_notFound");
            response.setDescription("La foto no existe en el sistema.");
            return response;
        }

        if (usuario == null) {
            response.setIsSuccesful(false);
            response.setMessage("user_notFound");
            response.setDescription("El usuario indicado no existe en el sistema.");
            return response;
        }

        // Si todo se cumple hasta aqui, buscamos el registro del favorito con el id de la foto y el id del usuario:
        var favorite = favoritePhotoDao.findByUserIdAndPhotoId(usuario.getId(), photo.getId());

        if (favorite == null) {
            response.setIsSuccesful(false);
            response.setMessage("save_notExists");
            response.setDescription(
                    "No existe un guardado de esa foto por parte del usuario ".concat(usuario.getUsername()));
            return response;
        }

        // Se elimina:
        favoritePhotoDao.delete2(favorite.getId());

        response.setIsSuccesful(true);
        response.setMessage("remove_success");
        response.setDescription("La foto guardada se removio con exito.");

        return response;
    }

    // Metodo para obtener todas las fotos guardadas en favoritos, pageadas
    @Override
    @Transactional(readOnly = true)
    public Page<FavoritePhoto> obtenerTodosLosFavoritosPagueados(Long id, Pageable pageable) {
        return favoritePhotoDao.findAllByUser_id(id, pageable);
    }

    // Metodo para obtener fotos buscadas por una keyword, pageadas
    @Override
    @Transactional(readOnly = true)
    public Page<Photo> obtenerFotosPorKeyword(String keyword, Pageable pageable) {
        return photoDao.findPhotosByKeywordLike(keyword, pageable);
    }

    @Transactional(readOnly = true)
    // Metodo para encontrar que foto tiene mas likes, desde la tabla de likes_photo, usando una consulta SQL nativa:
    public List<FotosConMasLikes> findAllOrderByLikesCount(int sort, Integer limit, Long user_id) {
        // 1 para ascendente y 0 para descendente (sort), el limite establece la cantidad de
        // registros resultantes y el user_id solo es necesario
        // si necesitamos las fotos de un solo usuario. El limite puede ser nulo y asi
        // indicaremos que no existira un limite, para que nos muestre todos los
        // registros.

        // LA CONSULTA SOLO MUESTRA LA CANTIDAD DE VECES QUE SE REPITEN UNOS REGISTROS DE LIKES PARA UNA FOTO EN ESPECIFICO, PERO CARGANDO EL ID DE ESA FOTO.
        if (limit != null) {
            if (user_id != null) {
                if (sort == 0) {
                    var fotosId = jdbc.query(
                            "SELECT photo_id FROM likes_photo where user_id = " + user_id.toString()
                                    + " GROUP BY photo_id ORDER BY COUNT(*) DESC LIMIT "
                                            .concat(limit.toString()),
                            new BeanPropertyRowMapper<>(FotosConMasLikes.class));
                    return fotosId;
                } else {
                    var fotosId = jdbc.query(
                            "SELECT photo_id FROM likes_photo where user_id = " + user_id.toString()
                                    + " GROUP BY photo_id ORDER BY COUNT(*) ASC LIMIT "
                                            .concat(limit.toString()),
                            new BeanPropertyRowMapper<>(FotosConMasLikes.class));
                    return fotosId;
                }
            }
            if (sort == 0) {
                var fotosId = jdbc.query(
                        "SELECT photo_id FROM likes_photo GROUP BY photo_id ORDER BY COUNT(*) DESC LIMIT "
                                .concat(limit.toString()),
                        new BeanPropertyRowMapper<>(FotosConMasLikes.class));
                return fotosId;
            } else {
                var fotosId = jdbc.query(
                        "SELECT photo_id FROM likes_photo GROUP BY photo_id ORDER BY COUNT(*) ASC LIMIT "
                                .concat(limit.toString()),
                        new BeanPropertyRowMapper<>(FotosConMasLikes.class));
                return fotosId;
            }
        } else {
            if (user_id != null) {
                if (sort == 0) {
                    var fotosId = jdbc.query(
                            "SELECT photo_id FROM likes_photo where user_id = " + user_id.toString()
                                    + " GROUP BY photo_id ORDER BY COUNT(*) DESC",
                            new BeanPropertyRowMapper<>(FotosConMasLikes.class));
                    return fotosId;
                } else {
                    var fotosId = jdbc.query(
                            "SELECT photo_id FROM likes_photo where user_id = " + user_id.toString()
                                    + " GROUP BY photo_id ORDER BY COUNT(*) ASC",
                            new BeanPropertyRowMapper<>(FotosConMasLikes.class));
                    return fotosId;
                }
            }
            if (sort == 0) {
                var fotosId = jdbc.query(
                        "SELECT photo_id FROM likes_photo GROUP BY photo_id ORDER BY COUNT(*) DESC",
                        new BeanPropertyRowMapper<>(FotosConMasLikes.class));
                return fotosId;
            } else {
                var fotosId = jdbc.query(
                        "SELECT photo_id FROM likes_photo GROUP BY photo_id ORDER BY COUNT(*) ASC",
                        new BeanPropertyRowMapper<>(FotosConMasLikes.class));
                return fotosId;
            }
        }
    }

    // Este metodo recibe la lista de fotos ordenada de manera descendente o ascendente, en funcion a la cantidad de likes (SOLO EL DB_ID)
    @Override
    @Transactional(readOnly = true)
    public List<Photo> fotosConMasLikes() {
        var fotosId = findAllOrderByLikesCount(0, 27, null);
        List<Photo> photos = new ArrayList<Photo>();
        // Buscamos cada foto por su db_id, para posteriormente añadirla a una lista:
        fotosId.forEach(id -> {
            var photo = photoDao.findById(id.getPhoto_id()).get();
            photos.add(photo);
        });
        return photos;
    }

}
