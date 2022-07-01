package com.leancoder.photogallery.models.dao;

import com.leancoder.photogallery.models.entities.user.User;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioDao extends CrudRepository<User, Long>{
    
    public User findByUsername(String username);

    public User findByEmail(String email);

    @Modifying
    @Query(value = "DELETE FROM users where id = :id", nativeQuery = true)
    public void deleteById2(@Param("id") Long id);

}
