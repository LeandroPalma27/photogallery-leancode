package com.leancoder.photogallery.models.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.leancoder.photogallery.models.entities.photo.FavoritePhoto;

// Repositorio DAO para las transacciones de la tabla favoritos
@Repository
public interface IFavoritePhotoDao extends PagingAndSortingRepository<FavoritePhoto, Long>{

    // Todos estos metodos usan consultas SQL nativas

    // Metodo para buscar fotos guardadas en favoritos, por parte de un usuario (incluye pagueo):
    @Query(value="SELECT * FROM favorite_photo where user_id = :user_id", nativeQuery = true)
    public Page<FavoritePhoto> findAllByUser_id(@Param("user_id") Long user_id, org.springframework.data.domain.Pageable pageable);
    
    // Metodo para encontrar un registro en los favoritos, para asi poder eliminarlo:
    @Query(nativeQuery = true, value = "SELECT * FROM favorite_photo WHERE user_id = :user_id and photo_id = :photo_id")
    public FavoritePhoto findByUserIdAndPhotoId(@Param(value = "user_id") Long user_id, @Param(value = "photo_id") Long photo_id);

    // Metodo opcional para eliminar un registro de favoritos:
    @Modifying
    @Query(value = "DELETE FROM favorite_photo WHERE id = :id", nativeQuery = true)
    public void delete2(@Param(value = "id") Long id);

    // Metodo opcional para eliminar un registro de favoritos:
    @Modifying
    @Query(value = "DELETE FROM favorite_photo WHERE user_id = :id", nativeQuery = true)
    public void deleteByUser_Id(@Param(value = "id") Long user_id);

    // Metodo para borrar un registro de favoritos por el id db_id de la foto:
    @Modifying
    @Query(value = "DELETE FROM favorite_photo WHERE photo_id = :id", nativeQuery = true)
    public void deleteByPhoto_Id(@Param(value = "id") Long photo_id);

}
