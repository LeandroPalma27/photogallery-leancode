package com.leancoder.photogallery.models.services.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import com.leancoder.photogallery.custom.mail_sender.IEmailService;
import com.leancoder.photogallery.models.dao.IGenderUserDao;
import com.leancoder.photogallery.models.dao.IRoleUserDao;
import com.leancoder.photogallery.models.dao.IUsuarioDao;
import com.leancoder.photogallery.models.domains.functionalities.current_date.CurrentDateBean;
import com.leancoder.photogallery.models.domains.responses.UpdateOrRegisterDetailsResponse;
import com.leancoder.photogallery.models.domains.validators.NameAndLastNameOfUserValidator;
import com.leancoder.photogallery.models.domains.validators.PasswordUserValidator;
import com.leancoder.photogallery.models.domains.validators.UserRegisterDomainValidator;
import com.leancoder.photogallery.models.entities.user.GenderUser;
import com.leancoder.photogallery.models.entities.user.User;
import com.leancoder.photogallery.models.entities.verification.VerificationRecords;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/* 
    * Clase service que implementa toda la logica de negocio para las consultas y transacciones de un usuario que se registra en la
      aplicacion.
 */
@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    IUsuarioDao usuarioDao;

    @Autowired
    IGenderUserDao genderUserDao;

    @Autowired
    IRoleUserDao roleDao;

    @Autowired
    IEmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String preToken = "tatitkm";

    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse registrarUsuario(UserRegisterDomainValidator usuario) {

        UpdateOrRegisterDetailsResponse detail = new UpdateOrRegisterDetailsResponse();

        if (usuarioDao.findByEmail(usuario.getEmail()) == null) {

            if (usuarioDao.findByUsername(usuario.getUsername()) == null) {

                var generoEscogido = obtenerGeneroPorId(usuario.getGenderId());

                if (generoEscogido != null) {
                    CurrentDateBean currentDate = new CurrentDateBean("yyyy-MM-dd HH:mm:ss");

                    User usu = new User();


                    // SI LA CUENTA NO SE ACTIVA PASADOS LOS 3 DIAS, SE ELMINA, IGUAL EL REGISTRO DE ACTIVACION DE CUENTA
                    VerificationRecords verificator = new VerificationRecords();
                    // Se genera token y luego se genera un registro para que se pueda cargar una vista con un boton que active la cuenta(TOKEN COMO PARAMETRO)
                    // El registro debe contener datos del usuario, como el username, los nombres con apellidos, la fecha, id, el token, tambien si la verificacion esta en activo y el email
                    // Si el registro no existe, la aplicacion debe cargar una vista que diga que no existe una verificaion pendiente o que quiza ya haya caducado
                    var token = passwordEncoder.encode(preToken);

                    verificator.setToken(token);
                    verificator.setFechaRegistro(currentDate.getCurrentDate());
                    verificator.setEmail(usuario.getEmail());
                    verificator.setEnabled(true);
                    verificator.setUsername(usuario.getUsername());
                    verificator.setVerificationType("email");


                    var role = roleDao.findById((long) 2).get();
                    usu.setNombre(usuario.getNombre());
                    usu.setApellidos(usuario.getApellidos());
                    usu.setEmail(usuario.getEmail());
                    usu.setPassword(passwordEncoder.encode(usuario.getPassword()));
                    usu.setUsername(usuario.getUsername());
                    usu.setEnabled(false);
                    usu.setFechaRegistro(currentDate.getCurrentDate());
                    usu.setGender(generoEscogido);
                    usu.setRole(role);
                    usu.setDescription("My description is...");

                    usuarioDao.save(usu);

                    return detail;

                }
                
                detail.setName("gender_error");
                detail.setMessage("No cambie la codificacion del formulario.");
                return detail;
            }

            detail.setName("duplicated_username");
            detail.setMessage("El username ingresado ya esta registrado.");

            return detail;
        }

        detail.setName("duplicated_email");
        detail.setMessage("El email ingresado ya esta registrado.");

        return detail;
    }

    @Override
    @Transactional(readOnly = true)
    public User obtenerUsuarioPorUsername(String username) {
        return usuarioDao.findByUsername(username);
    }

    @Override
    @Transactional
    public void eliminarUsuario(User usuario) {
        usuarioDao.delete(usuario);
    }

    @Override
    @Transactional
    public void actualizarDescripcion(User usuario, String descripcion) {
        usuario.setDescription(descripcion);
        usuarioDao.save(usuario);
        try {
            Map<String, Object> model = new HashMap<String, Object>();
            emailService.sendMessageUsingThymeleafTemplate(usuario.getEmail(), "CAMBIO DE DESCRIPCION", "prueba", model);
        } catch (MessagingException e) {
            System.out.println("ERRROR AL ENVIAR EMAIL.");
        }
    }

    @Override
    @Transactional
    public void actualizarNombreAndApellidos(User usuario, NameAndLastNameOfUserValidator details) {
        usuario.setNombre(details.getName());
        usuario.setApellidos(details.getLastname());
        usuarioDao.save(usuario);
    }

    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse actualizarUsername(User usuario, String newUsername) {

        UpdateOrRegisterDetailsResponse fail = new UpdateOrRegisterDetailsResponse();

        if (usuarioDao.findByUsername(newUsername) == null) {

            usuario.setUsername(newUsername);

            usuarioDao.save(usuario);

            fail.setName("successful_process");

            return fail;

        } else {

            if (usuario.getUsername().equals(newUsername)) {

                fail.setName("same_username");
                fail.setMessage("No coloque el mismo username.");

                return fail;
            }

            fail.setName("duplicated_username");
            fail.setMessage("El username ingresado ya esta registrado.");

            return fail;
        }

    }

    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse actualizarConstraseña(PasswordUserValidator detailPassword,
            User usuario) {

        UpdateOrRegisterDetailsResponse res = new UpdateOrRegisterDetailsResponse();

        if (passwordEncoder.matches(detailPassword.getOldpass(), usuario.getPassword())) {

            if (passwordEncoder.matches(detailPassword.getNewpass(), usuario.getPassword())) {

                res.setName("same_password");
                res.setMessage("La contraseña nueva no puede ser igual a la contraseña actual.");

                return res;

            } else {

                var nuevaContraseñaCifrada = passwordEncoder.encode(detailPassword.getNewpass());
                usuario.setPassword(nuevaContraseñaCifrada);

                usuarioDao.save(usuario);

                res.setMessage(nuevaContraseñaCifrada);

                return res;
            }

        }

        res.setName("incorrect_password");
        res.setMessage("La contraseña ingresada no coincide con la que tenemos registrada en el sistema.");

        return res;

    }

    @Override
    public List<GenderUser> listarGenerosUsuario() {
        List<GenderUser> listaGeneros = new ArrayList<GenderUser>();
        genderUserDao.findAll().forEach(listaGeneros::add);
        return listaGeneros;
    }

    public GenderUser obtenerGeneroPorId(Long id) {
        if (!genderUserDao.findById(id).isEmpty()) {
            return genderUserDao.findById(id).get();
        }
        return null;
    }

}
