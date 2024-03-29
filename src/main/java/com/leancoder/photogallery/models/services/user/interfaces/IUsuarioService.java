package com.leancoder.photogallery.models.services.user.interfaces;

import java.util.List;

import com.leancoder.photogallery.models.domains.responses.UpdateOrRegisterDetailsResponse;
import com.leancoder.photogallery.models.domains.validators.NameAndLastNameOfUserValidator;
import com.leancoder.photogallery.models.domains.validators.PasswordUserValidator;
import com.leancoder.photogallery.models.domains.validators.UserRegisterDomainValidator;
import com.leancoder.photogallery.models.entities.user.GenderUser;
import com.leancoder.photogallery.models.entities.user.User;
import com.leancoder.photogallery.models.entities.verification.VerificationRecords;

public interface IUsuarioService {
    
    public UpdateOrRegisterDetailsResponse registrarUsuario(UserRegisterDomainValidator usuario);

    public User obtenerUsuarioPorUsername(String username);

    public void actualizarNombreAndApellidos(User usuario, NameAndLastNameOfUserValidator details);

    public void eliminarUsuario(User usuario);

    public UpdateOrRegisterDetailsResponse actualizarUsername(User usuario, String newUsername);

    public UpdateOrRegisterDetailsResponse actualizarConstraseña(PasswordUserValidator detailPassword, User usuario);

    public void actualizarDescripcion(User usuario, String descripcion);

    public List<GenderUser> listarGenerosUsuario();

    public UpdateOrRegisterDetailsResponse VerificarUsuario(String token);

    public VerificationRecords crearPeticionParaCambioContraseña(String email);

    public Boolean cambiarContraseñaOlvidada(VerificationRecords record, String nuevaContraseña);

    public Boolean solicitarVerificacionCuenta(String username);

    public Boolean actualizarEmail(String email, String username);

}
