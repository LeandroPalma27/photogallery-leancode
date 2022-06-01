package com.leancoder.photogallery.models.dao;

import com.leancoder.photogallery.models.entities.user.User;

import org.springframework.data.repository.CrudRepository;

public interface IUsuarioDao extends CrudRepository<User, Long>{
    
    public User findByUsername(String username);

    public User findByEmail(String email);

}
