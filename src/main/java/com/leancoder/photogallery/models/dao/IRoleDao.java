package com.leancoder.photogallery.models.dao;

import com.leancoder.photogallery.models.entity.Role;

import org.springframework.data.repository.CrudRepository;

public interface IRoleDao extends CrudRepository<Role, Long> {
    
}
