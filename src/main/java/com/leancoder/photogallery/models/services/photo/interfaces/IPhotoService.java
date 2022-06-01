package com.leancoder.photogallery.models.services.photo.interfaces;

import java.io.IOException;
import java.util.List;

import com.leancoder.photogallery.models.domains.responses.UpdateOrRegisterDetailsResponse;
import com.leancoder.photogallery.models.domains.validators.PhotoUpdaterValidator;
import com.leancoder.photogallery.models.domains.validators.PhotoUploaderValidator;
import com.leancoder.photogallery.models.entities.photo.Photo;
import com.leancoder.photogallery.models.entities.user.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPhotoService {
    
    public UpdateOrRegisterDetailsResponse registrarFoto(PhotoUploaderValidator photoValidator, User usuario);

    public UpdateOrRegisterDetailsResponse registrarFotoPerfil(PhotoUploaderValidator photoValidator, User usuario);

    public Photo buscarFoto(String public_id);

    public Iterable<Photo> obtenerTodasLasFotos();

    public Page<Photo> obtenerTodasLasFotosPagueadas(Pageable pageable);

    public Page<Photo> obtenerTodasLasFotosDeUnUsuarioPagueadas(Long user_id, Pageable pageable);

    public UpdateOrRegisterDetailsResponse actualizarDetallesFoto(String uploadId, PhotoUpdaterValidator photoValidator);

    public UpdateOrRegisterDetailsResponse eliminarFoto(String uploadId, Photo photo) throws IOException;

    public UpdateOrRegisterDetailsResponse removerFotoDePerfil(String uploadId);

    public UpdateOrRegisterDetailsResponse establecerComoFotoDePerfil(String uploadId, User usuario);

}
