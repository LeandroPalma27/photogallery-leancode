package com.leancoder.photogallery.models.dao;

import java.util.List;

import com.leancoder.photogallery.models.entities.photo.RolePhoto;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface IRolePhotoDao extends CrudRepository<RolePhoto, Long>{
    
    // Metodo con consulta nativa para buscar algun rol de foto por el db_id de la foto:
    @Query(value = "SELECT * FROM roles_photo WHERE photo_id = :photo_id", nativeQuery = true)
    public List<RolePhoto> findyByPhoto_id(@Param("photo_id") Long photo_id);

    // Se usa la etiqueta @Modifying ya que se necesita en caso de hacer un INSERT, UPDATE o DELETE.
    @Modifying
    @Query(value = "DELETE FROM roles_photo WHERE id = :id", nativeQuery = true)
    public void deleteById2(@Param("id") Long id);

}
