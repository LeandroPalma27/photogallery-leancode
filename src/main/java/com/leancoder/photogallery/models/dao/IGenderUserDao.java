package com.leancoder.photogallery.models.dao;

import com.leancoder.photogallery.models.entities.user.GenderUser;

import org.springframework.data.repository.CrudRepository;

public interface IGenderUserDao extends CrudRepository<GenderUser, Long>{

}
