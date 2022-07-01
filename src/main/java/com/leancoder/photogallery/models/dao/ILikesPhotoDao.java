package com.leancoder.photogallery.models.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.leancoder.photogallery.models.entities.photo.LikesPhoto;

@Repository
public interface ILikesPhotoDao extends PagingAndSortingRepository<LikesPhoto, Long>{
    
    @Query(nativeQuery = true, value = "SELECT * FROM likes_photo WHERE user_id = :user_id and photo_id = :photo_id")
    public LikesPhoto findByUserIdAndPhotoId(@Param(value = "user_id") Long user_id, @Param(value = "photo_id") Long photo_id);

    @Modifying
    @Query(value = "DELETE FROM likes_photo WHERE id = :id", nativeQuery = true)
    public void delete2(@Param(value = "id") Long id);

    @Modifying
    @Query(value = "DELETE FROM likes_photo WHERE user_id = :id", nativeQuery = true)
    public void deleteByUser_Id(@Param(value = "id") Long user_id);

}
