package com.leancoder.photogallery.models.services.photo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.leancoder.photogallery.custom.rest_provider.cloudinary.interfaces.ICloudinaryProvider;
import com.leancoder.photogallery.models.dao.IFavoritePhotoDao;
import com.leancoder.photogallery.models.dao.ILikesPhotoDao;
import com.leancoder.photogallery.models.dao.IPhotoDao;
import com.leancoder.photogallery.models.dao.IRolePhotoDao;
import com.leancoder.photogallery.models.dao.IUsuarioDao;
import com.leancoder.photogallery.models.domains.functionalities.current_date.CurrentDateBean;
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

    private Photo subirFotoAlServidor(PhotoUploaderValidator validator) {

        CurrentDateBean currentDate = new CurrentDateBean("yyyy-MM-dd HH:mm:ss");
        var photoUploaded = cloudinaryService.upload(validator.getFile());

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
        return null;
    }

    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse registrarFoto(PhotoUploaderValidator photoValidator, User usuario) {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();
        var photo = subirFotoAlServidor(photoValidator);

        if (photo != null) {
            photo.setUser(usuario);
            photo.setRoles(Arrays.asList(new RolePhoto("ROLE_NORMAL", photo)));
            photoDao.save(photo);
            response.setName("successful");
            return response;
        }
        response.setName("unknown_error");
        response.setMessage("Ocurrio un error inesperado, intentelo de nuevo.");
        return response;

    }

    @Override
    @Transactional(readOnly = true)
    public Photo buscarFoto(String public_id) {
        return photoDao.findByUpload_id(public_id);
    }

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

        var photoFound = buscarFoto(uploadId);

        if (photoFound != null) {
            photoFound.setTitle(photoValidator.getTitle());
            photoFound.setDescription(photoValidator.getDescription());

            photoDao.save(photoFound);

            response.setName("successful_update");
            response.setMessage("Se actualizo los detalles.");
            return response;
        }

        response.setName("error_update");
        response.setMessage("Ocurrio un error, intentelo mas tarde.");
        return response;
    }

    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse eliminarFoto(Photo photo) throws IOException {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();

        var res = cloudinaryService.delete(photo.getUploadId());
        
        if ((boolean) res.get("isDelete")) {
            photoDao.deleteById2(photo.getId());
            response.setName("successful_delete");
            response.setMessage("La foto se elimino con exito.");
            return response;
        }
        response.setName("error_delete");
        response.setMessage("Ocurrio algun error al borrar la foto, intentelo mas tarde.");
        return response;
    }

    // Se utiliza para paguear la lista de todas las fotos.
    @Override
    @Transactional(readOnly = true)
    public Page<Photo> obtenerTodasLasFotosPagueadas(Pageable pageable) {
        return photoDao.findAll(pageable);
    }

    // Se utiliza para paguear la lista de todas las fotos de un solo usuario.
    @Override
    @Transactional(readOnly = true)
    public Page<Photo> obtenerTodasLasFotosDeUnUsuarioPagueadas(Long user_id, Pageable pageable) {
        return photoDao.findByUser_id(user_id, pageable);
    }

    // El formulario que procesa el registro esta en la vista del perfil del usuario.
    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse registrarFotoPerfil(PhotoUploaderValidator photoValidator, User usuario) {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();
        var photo = subirFotoAlServidor(photoValidator);

        if (photo != null) {

            var fotosUsuario = photoDao.findByUser_id(usuario.getId());
            principal: for (var foto : fotosUsuario) {
                if (foto.getRoles().size() > 1) {
                    for (var role : foto.getRoles()) {
                        if (role.getRole().equals("ROLE_PROFILE")) {
                            rolePhotoDao.deleteById2(role.getId());
                            break principal;
                        }
                    }
                } else {
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

    // Se puede remover desde el perfil del usuario o desde la lista de fotos del usuario
    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse removerFotoDePerfil(String uploadId) {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();

        var foto = photoDao.findByUpload_id(uploadId);
        var rolesDeLaFotoDePerfilActual = rolePhotoDao.findyByPhoto_id(foto.getId());

        if (foto != null) {
            for (var rol : rolesDeLaFotoDePerfilActual) {
                if (rol.getRole().equals("ROLE_PROFILE")) {
                    rolePhotoDao.deleteById2(rol.getId());
                    response.setName("successful_remove");
                    response.setMessage("La foto se quito con exito.");
                    return response;
                } else {
                    continue;
                }
            }
        }

        response.setName("photo_notFound");
        response.setMessage("La foto no existe.");
        return response;

    }

    // Solo se hace desde la lista de fotos de un usuario.
    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse establecerComoFotoDePerfil(String uploadId, User usuario) {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();

        var fotoActual = photoDao.findByUpload_id(uploadId);
        var fotosUsuario = photoDao.findByUser_id(usuario.getId());

        if (fotoActual != null) {

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

            RolePhoto role = new RolePhoto();
            role.setPhoto(fotoActual);
            role.setRole("ROLE_PROFILE");

            var rolesFotoActual = fotoActual.getRoles();
            rolesFotoActual.add(role);

            fotoActual.setRoles(rolesFotoActual);
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

        LikesPhoto like = new LikesPhoto();
        like.setPhoto(photo);
        like.setUser(usuario);
        like.setDate(currentDate.getCurrentDate());
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

        var like = likesPhotoDao.findByUserIdAndPhotoId(usuario.getId(), photo.getId());

        if (like == null) {
            response.setIsSuccesful(false);
            response.setMessage("like_notExists");
            response.setDescription("No existe un like del usuario ".concat(usuario.getUsername()));
            return response;
        }

        likesPhotoDao.delete2(like.getId());

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

    // Metodo para transaccionar el quite de una foto de favoritos, por parte de un usuario.
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

        var favorite = favoritePhotoDao.findByUserIdAndPhotoId(usuario.getId(), photo.getId());

        if (favorite == null) {
            response.setIsSuccesful(false);
            response.setMessage("save_notExists");
            response.setDescription("No existe un guardado de esa foto por parte del usuario ".concat(usuario.getUsername()));
            return response;
        }

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

}
