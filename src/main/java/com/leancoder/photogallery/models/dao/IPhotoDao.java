package com.leancoder.photogallery.models.dao;

import java.util.List;

import com.leancoder.photogallery.models.entities.photo.Photo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IPhotoDao extends PagingAndSortingRepository<Photo, Long>{

    @Query(value = "SELECT * FROM photos ORDER BY fecha_registro DESC", nativeQuery = true)
    public Page<Photo> findAllOrderByDateDesc(Pageable pageable);

    @Query(value = "SELECT * FROM photos where user_id = :user_id ORDER BY fecha_registro DESC", nativeQuery = true)
    public Page<Photo> findAllOrderByDateDescAndUser_Id(@Param("user_id") Long user_id, Pageable pageable);

    @Query(value = "SELECT * FROM photos ORDER BY fecha_registro ASC", nativeQuery = true)
    public Page<Photo> findAllOrderByDateAsc(Pageable pageable);

    @Query(value = "SELECT * FROM photos where user_id = :user_id ORDER BY fecha_registro ASC", nativeQuery = true)
    public Page<Photo> findAllOrderByDateAscAndUser_Id(@Param("user_id") Long user_id, Pageable pageable);
    
    @Query(value = "SELECT * FROM photos WHERE upload_id = :upload_id", nativeQuery = true)
    public Photo findByUpload_id(@Param("upload_id") String upload_id);

    @Query(value="SELECT * FROM photos where user_id = :user_id", nativeQuery = true)
    public Page<Photo> findByUser_id(@Param("user_id") Long user_id, Pageable pageable);

    @Query(value="SELECT * FROM photos where user_id = :user_id", nativeQuery = true)
    public List<Photo> findByUser_id(@Param("user_id") Long user_id);

    @Modifying
    @Query(value = "DELETE FROM photos where db_id = :id", nativeQuery = true)
    public void deleteById2(@Param("id") Long id);

    @Query(value = "SELECT * FROM photos JOIN users u where title like %:keyword% or u.username like %:keyword% or u.nombre like %:keyword%", nativeQuery = true)
    public Page<Photo> findPhotosByKeywordLike(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM photos WHERE user_id = :id", nativeQuery = true)
    public void deleteByUser_Id(@Param(value = "id") Long user_id);

}
