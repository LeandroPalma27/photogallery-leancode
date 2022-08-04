package com.leancoder.photogallery.models.dao;

import com.leancoder.photogallery.models.entities.user.User;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Repositorio DAO para las transacciones de la tabla usuarios:
@Repository
public interface IUsuarioDao extends CrudRepository<User, Long>{
    
    // Metodo HIBERNATE para buscar por el username:
    public User findByUsername(String username);

    // Metodo HIBERNATE para buscar por el email del usuario:
    public User findByEmail(String email);

    // Metodo consulta sql nativa para eliminar un usuario:
    @Modifying
    @Query(value = "DELETE FROM users where id = :id", nativeQuery = true)
    public void deleteById2(@Param("id") Long id);

}
