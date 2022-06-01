package com.leancoder.photogallery.models.services.photo;

import java.io.IOException;
import java.util.Arrays;

import com.leancoder.photogallery.models.dao.IPhotoDao;
import com.leancoder.photogallery.models.dao.IRolePhotoDao;
import com.leancoder.photogallery.models.domains.functionalities.current_date.CurrentDateBean;
import com.leancoder.photogallery.models.domains.responses.UpdateOrRegisterDetailsResponse;
import com.leancoder.photogallery.models.domains.validators.PhotoUpdaterValidator;
import com.leancoder.photogallery.models.domains.validators.PhotoUploaderValidator;
import com.leancoder.photogallery.models.entities.photo.Photo;
import com.leancoder.photogallery.models.entities.photo.RolePhoto;
import com.leancoder.photogallery.models.entities.user.User;
import com.leancoder.photogallery.models.services.photo.interfaces.IPhotoService;
import com.leancoder.photogallery.provider.cloudinary.interfaces.ICloudinaryProvider;

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
    private IRolePhotoDao rolePhotoDao;

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

    /* private Boolean quitarRolPerfil (List<Photo> fotosUsuario) {

        principal: for (var foto : fotosUsuario) {
            if (foto.getRoles().size() > 1) {
                for (var role : foto.getRoles()) {
                    if (role.getRole().equals("ROLE_PROFILE")) {
                        rolePhotoDao.deleteById2(role.getId());
                        return true;
                    } else {
                        continue principal;
                    }
                }
            } else {
                continue;
            }

        }
        return false;

    } */

    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse registrarFoto(PhotoUploaderValidator photoValidator, User usuario) {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();
        var photo = subirFotoAlServidor(photoValidator);

        if (photo != null) {
            photo.setUsuario(usuario);
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
    public UpdateOrRegisterDetailsResponse eliminarFoto(String uploadId, Photo photo) throws IOException {

        UpdateOrRegisterDetailsResponse response = new UpdateOrRegisterDetailsResponse();

        var res = cloudinaryService.delete(uploadId);

        if ((boolean) res.get("isDelete")) {
            photoDao.delete(photo);
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
            photo.setUsuario(usuario);
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

}
