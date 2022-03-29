package com.leancoder.photogallery.models.dao;

import com.leancoder.photogallery.models.entity.Usuario;

import org.springframework.data.repository.CrudRepository;

public interface IUsuarioDao extends CrudRepository<Usuario, Long>{
    
    public Usuario findByUsername(String username);

    public Usuario findByEmail(String email);

}
