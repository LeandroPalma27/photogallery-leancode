package com.leancoder.photogallery.models.dao;

import com.leancoder.photogallery.models.entities.user.RoleUser;

import org.springframework.data.repository.CrudRepository;

public interface IRoleUserDao extends CrudRepository<RoleUser, Long> {
    
}
