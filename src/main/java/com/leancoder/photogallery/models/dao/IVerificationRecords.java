package com.leancoder.photogallery.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.leancoder.photogallery.models.entities.verification.VerificationRecords;

@Repository
public interface IVerificationRecords extends CrudRepository<VerificationRecords, Long>{
    
    public VerificationRecords findByToken(String token);

    @Query(nativeQuery = true, value = "SELECT * FROM verification_records WHERE username = :username and verification_type = :type and enabled = :enabled")
    public VerificationRecords findByUsernameTypeAndEnabled(@Param(value = "username") String username, @Param(value = "type") String type, @Param(value = "enabled") Integer enabled);

}
