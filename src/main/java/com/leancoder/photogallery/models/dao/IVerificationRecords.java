package com.leancoder.photogallery.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.leancoder.photogallery.models.entities.verification.VerificationRecords;

// Repositorio DAO para el manejo de transacciones en la tabla de registros de peticiones de verificacion de cuenta y cambio de contraseña
@Repository
public interface IVerificationRecords extends CrudRepository<VerificationRecords, Long>{
    
    // Metodo HIBERNATE para buscar por el token:
    public VerificationRecords findByToken(String token);

    // Metodo para buscar un registro por el username, tipo de verificacion (verificacion de cuenta o cambio de contraseña) y estado (peticion valida o invalida):
    @Query(nativeQuery = true, value = "SELECT * FROM verification_records WHERE username = :username and verification_type = :type and enabled = :enabled")
    public VerificationRecords findByUsernameTypeAndEnabled(@Param(value = "username") String username, @Param(value = "type") String type, @Param(value = "enabled") Integer enabled);

}
