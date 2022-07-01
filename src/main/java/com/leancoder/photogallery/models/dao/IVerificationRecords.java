package com.leancoder.photogallery.models.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.leancoder.photogallery.models.entities.verification.VerificationRecords;

@Repository
public interface IVerificationRecords extends CrudRepository<VerificationRecords, Long>{
    
    public VerificationRecords findByToken(String token);

}
