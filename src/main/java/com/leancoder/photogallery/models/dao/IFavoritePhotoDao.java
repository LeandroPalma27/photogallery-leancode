package com.leancoder.photogallery.models.dao;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.leancoder.photogallery.models.entities.photo.FavoritePhoto;

@Repository
public interface IFavoritePhotoDao extends PagingAndSortingRepository<FavoritePhoto, Long>{

    @Query(value="SELECT * FROM favorite_photo where user_id = :user_id", nativeQuery = true)
    public Page<FavoritePhoto> findAllByUser_id(@Param("user_id") Long user_id, org.springframework.data.domain.Pageable pageable);
    
    @Query(nativeQuery = true, value = "SELECT * FROM favorite_photo WHERE user_id = :user_id and photo_id = :photo_id")
    public FavoritePhoto findByUserIdAndPhotoId(@Param(value = "user_id") Long user_id, @Param(value = "photo_id") Long photo_id);

    @Modifying
    @Query(value = "DELETE FROM favorite_photo WHERE id = :id", nativeQuery = true)
    public void delete2(@Param(value = "id") Long id);

}
