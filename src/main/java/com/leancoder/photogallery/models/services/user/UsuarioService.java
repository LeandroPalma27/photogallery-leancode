package com.leancoder.photogallery.models.services.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.leancoder.photogallery.custom.mail_sender.IEmailService;
import com.leancoder.photogallery.models.dao.IFavoritePhotoDao;
import com.leancoder.photogallery.models.dao.IGenderUserDao;
import com.leancoder.photogallery.models.dao.ILikesPhotoDao;
import com.leancoder.photogallery.models.dao.IPhotoDao;
import com.leancoder.photogallery.models.dao.IRoleUserDao;
import com.leancoder.photogallery.models.dao.IUsuarioDao;
import com.leancoder.photogallery.models.dao.IVerificationRecords;
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
    IVerificationRecords verificationRecords;

    @Autowired
    IGenderUserDao genderUserDao;

    @Autowired
    IRoleUserDao roleDao;

    @Autowired
    ILikesPhotoDao likesPhotoDao;

    @Autowired
    IFavoritePhotoDao favoritePhotoDao;

    @Autowired
    IPhotoDao photoDao;

    @Autowired
    IEmailService emailService;

    // Inyectamos el BCrypt para el cifrado de contraseñas y de la cadena generadora de tokens para peticiones de verificacion y cambio de contraseña:
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Cadena que sirve como una llave para obtener varios token a partir de un string comun (usando BCrypt)
    private String preToken = "tatitkm";

    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse registrarUsuario(UserRegisterDomainValidator usuario) {

        // La mayoria de las implementaciones de un service, responden con un bean para el manejo de un response personalizado:
        UpdateOrRegisterDetailsResponse detail = new UpdateOrRegisterDetailsResponse();

        // Antes de registrar un usuario, se verifica que no exista uno con el email del usuario a registrar:
        if (usuarioDao.findByEmail(usuario.getEmail()) == null) {

            // Luego verificamos que no exista un usuario con el mismo username, o que el usuario nuevo haya puesto como username "anonymousUser"
            if (usuarioDao.findByUsername(usuario.getUsername()) == null && !(usuario.getUsername().equals("anonymousUser"))) {

                // Buscamos el genero a marcar para el usuario a registrarse (se obtiene el id desde el formulario con un combo box)
                var generoEscogido = obtenerGeneroPorId(usuario.getGenderId());

                // Si el genero se encuentra disponible se registra al usuario:
                if (generoEscogido != null) {
                    CurrentDateBean currentDate = new CurrentDateBean("yyyy-MM-dd HH:mm:ss");

                    User usu = new User();


                    // SI LA CUENTA NO SE ACTIVA PASADOS LOS 3 DIAS, SE ELMINA, IGUAL EL REGISTRO DE ACTIVACION DE CUENTA
                    VerificationRecords verificator = new VerificationRecords();
                    // Se genera token y luego se genera un registro para que se pueda cargar una vista con un boton que active la cuenta(TOKEN COMO PARAMETRO)
                    // El registro debe contener datos del usuario, como el username, los nombres con apellidos, la fecha, id, el token, tambien si la verificacion esta en activo y el email
                    // Si el registro no existe, la aplicacion debe cargar una vista que diga que no existe una verificaion pendiente o que quiza ya haya caducado
                    var pre_token = passwordEncoder.encode(preToken);
                    var token = pre_token.replace("/", "");

                    verificator.setToken(token);
                    verificator.setFechaRegistro(currentDate.getCurrentDate());
                    verificator.setEmail(usuario.getEmail());
                    verificator.setEnabled(true);
                    verificator.setUsername(usuario.getUsername());
                    verificator.setVerificationType("email");

                    // Guardamos el registro de verificacion de cuenta (en este caso activo):
                    verificationRecords.save(verificator);

                    // Luego registrar el usuario en la base de datos, se intenta mandar el correo de verificacion al email ingresado:
                    try {
                        String url = "http://localhost:8080/verify-account/".concat(token);
                        Map<String, Object> model = new HashMap<String, Object>();
                        model.put("name", usuario.getNombre());
                        model.put("token", token);
                        model.put("link", url);
                        emailService.sendMessageUsingThymeleafTemplate(usuario.getEmail(), "Verificacion de correo", "email-verificator", model);
                    } catch (MessagingException e) {
                        // Si falla el envio:
                        System.out.println("ERRROR AL ENVIAR EMAIL.");
                    }

                    // Buscamos de los roles disponibles en la base de datos, para asignarselo al usuario:
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

                    // Guardamos al usuario en la base de datos:
                    usuarioDao.save(usu);

                    return detail;

                }
                


            // ERRORES QUE SE PODRIAN GENERAR: 
                
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
    // Eliminar un usuario implica tener que borrar toda la data relacionada a el, antes de borrar al usuario:
    public void eliminarUsuario(User usuario) {
        likesPhotoDao.deleteByUser_Id(usuario.getId());
        favoritePhotoDao.deleteByUser_Id(usuario.getId());
        photoDao.deleteByUser_Id(usuario.getId());
        usuarioDao.deleteById2(usuario.getId());
    }

    @Override
    @Transactional
    public void actualizarDescripcion(User usuario, String descripcion) {
        usuario.setDescription(descripcion);
        usuarioDao.save(usuario);
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

        // Si la contraseña ingresada en el formulario coincide con la contraseña actual:
        if (passwordEncoder.matches(detailPassword.getOldpass(), usuario.getPassword())) {

            // Y luego, si la contraseña nueva coincide con la actual, no se podria cambiar la contraseña:
            if (passwordEncoder.matches(detailPassword.getNewpass(), usuario.getPassword())) {

                res.setName("same_password");
                res.setMessage("La contraseña nueva no puede ser igual a la contraseña actual.");

                return res;

            } else {

                // Caso contrario:
                var nuevaContraseñaCifrada = passwordEncoder.encode(detailPassword.getNewpass());
                usuario.setPassword(nuevaContraseñaCifrada);

                usuarioDao.save(usuario);

                res.setMessage(nuevaContraseñaCifrada);

                return res;
            }

        }

        // Si la contraseña actual ingresada no coincide con la contraseña actual del usuario:
        res.setName("incorrect_password");
        res.setMessage("La contraseña ingresada no coincide con la que tenemos registrada en el sistema.");

        return res;

    }

    @Override
    @Transactional(readOnly = true)
    public List<GenderUser> listarGenerosUsuario() {
        List<GenderUser> listaGeneros = new ArrayList<GenderUser>();
        genderUserDao.findAll().forEach(listaGeneros::add);
        return listaGeneros;
    }

    @Transactional(readOnly = true)
    public GenderUser obtenerGeneroPorId(Long id) {
        if (!genderUserDao.findById(id).isEmpty()) {
            return genderUserDao.findById(id).get();
        }
        return null;
    }

    @Override
    @Transactional
    public UpdateOrRegisterDetailsResponse VerificarUsuario(String token) {
        UpdateOrRegisterDetailsResponse res = new UpdateOrRegisterDetailsResponse();
        // Empezamos buscando el registro de verificacion (debe estar activo):
        var verificator = verificationRecords.findByToken(token);
        if (verificator != null) {
            // Si esta activo:
            if (verificator.getEnabled()) {
                var usuario = usuarioDao.findByUsername(verificator.getUsername());
                if (usuario != null) {
                    // Por si acaso verificamos que el usuario no este verificado tambien:
                    if (!usuario.getEnabled()) {
                        // Cambiamos estados (usuario como verificado y el registro de verificacion como inactivo):
                        verificator.setEnabled(false);
                        usuario.setEnabled(true);
                        // Guardamos todo:
                        usuarioDao.save(usuario);
                        verificationRecords.save(verificator);


                // Mensajes RES correspondientes:
                        res.setName("successful_process");
                        res.setMessage("Se verifico con exito la cuenta.");
                        return res;
                    }
                    res.setName("user_verificado");
                    return res;
                }
                res.setName("user_notFound");
                return res;
            }
            res.setName("verificator_notValid");
            return res;
        }
        res.setName("verificator_notExists");
        return res;
    }

    @Override
    @Transactional
    public VerificationRecords crearPeticionParaCambioContraseña(String email) {
        // Buscamos que exista el usuario:
        var usuario = usuarioDao.findByEmail(email);
        // Y verificamos que este verificado:
        var usuarioExiste = usuario != null && usuario.getEnabled() == true ? true : false;

        // Si no existe o no esta verificado, se retorna null;
        if (!usuarioExiste) {
            return null;
        }

        // Caso contrario:
        CurrentDateBean currentDate = new CurrentDateBean("yyyy-MM-dd HH:mm:ss");
        VerificationRecords record = new VerificationRecords();
        var pre_token = passwordEncoder.encode(preToken);
        var token = pre_token.replace("/", "");
        record.setEmail(email);
        record.setEnabled(true);
        record.setUsername(usuario.getUsername());
        record.setVerificationType("change_password");
        record.setFechaRegistro(currentDate.getCurrentDate());
        record.setToken(token);
        verificationRecords.save(record);

        try {
            String url = "http://localhost:8080/change-password/".concat(token);
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("name", usuario.getNombre());
            model.put("token", token);
            model.put("link", url);
            emailService.sendMessageUsingThymeleafTemplate(usuario.getEmail(), "Cambio de contraseña", "forgot-pass", model);
        } catch (MessagingException e) {
            System.out.println("ERRROR AL ENVIAR EMAIL.");
        }

        return record;
    }

    @Override
    public Boolean cambiarContraseñaOlvidada(VerificationRecords record, String nuevaContraseña) {
        // Encontramos al usuario desde el registro de cambio de contraseña (contiene el campo usuario para saber que usuario solicito esa peticion de cambio):
        var usuario = usuarioDao.findByUsername(record.getUsername());
        // Obtenemos la nueva contraseña cifrada:
        var nuevaContraseñaCifrada = passwordEncoder.encode(nuevaContraseña);
        usuario.setPassword(nuevaContraseñaCifrada);
        // Colocamos a la peticion como ya inactiva:
        record.setEnabled(false);

        // Guardamos y en caso de fallar, retornamos false:
        try {
            verificationRecords.save(record);
        } catch (Exception e) {
            return false;
        }

        // Guardamos al usuario con la contraseña ya cambiada, y retornamos true:
        usuarioDao.save(usuario);
        return true;
    }

    @Override
    @Transactional
    // Este metodo se usa para cuando un usuario que no ha recibido el correo de verificacion al momento de crear una cuenta, pueda volver a solicitar ese correo.
    public Boolean solicitarVerificacionCuenta(String username) {
        // Buscamos si existe una verificacion pendiente (que no se envio al correo)
        var verificador = verificationRecords.findByUsernameTypeAndEnabled(username, "email", 1);
        var usuario = obtenerUsuarioPorUsername(username);
        // Si no existe y el usuario esta verificado, se retorna false:
        if (verificador == null || usuario.getEnabled()) {
            return false;
        }

        // Caso contrario, se elimina esa verificacion y se crea otra:
        verificationRecords.delete(verificador);

        CurrentDateBean currentDate = new CurrentDateBean("yyyy-MM-dd HH:mm:ss");
        VerificationRecords verificator = new VerificationRecords();
        var pre_token = passwordEncoder.encode(preToken);
        var token = pre_token.replace("/", "");
        verificator.setToken(token);
        verificator.setFechaRegistro(currentDate.getCurrentDate());
        verificator.setEmail(usuario.getEmail());
        verificator.setEnabled(true);
        verificator.setUsername(usuario.getUsername());
        verificator.setVerificationType("email");

        verificationRecords.save(verificator);

        try {
            String url = "http://localhost:8080/verify-account/".concat(token);
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("name", usuario.getNombre());
            model.put("token", token);
            model.put("link", url);
            emailService.sendMessageUsingThymeleafTemplate(usuario.getEmail(), "Verificacion de correo", "email-verificator", model);
            return true;
        } catch (MessagingException e) {
            System.out.println("ERRROR AL ENVIAR EMAIL.");
            return false;
        }
    }

    @Override
    // Al cambiar el email, la cuenta debe ser verificada como si recien estuviera creada:
    public Boolean actualizarEmail(String email, String username) {
        // Buscamos al usuario para cambiarle el email:
        var user = usuarioDao.findByUsername(username);
        // Verificamos que el email nuevo no este registrado en la base de datos:
        var existeEmail = usuarioDao.findByEmail(email) != null ? true : false;

        if (!existeEmail) {
            // Si el email no existe, buscamos si existe un registro de verificacion (por si acaso):
            var verificador = verificationRecords.findByUsernameTypeAndEnabled(username, "email", 1);
            if (verificador != null) {
                // Y si existe lo eliminamos:
                verificationRecords.delete(verificador);
            }
            CurrentDateBean currentDate = new CurrentDateBean("yyyy-MM-dd HH:mm:ss");
            VerificationRecords verificator = new VerificationRecords();
            var pre_token = passwordEncoder.encode(preToken);
            var token = pre_token.replace("/", "");
            verificator.setToken(token);
            verificator.setFechaRegistro(currentDate.getCurrentDate());
            verificator.setEmail(email);
            verificator.setEnabled(true);
            verificator.setUsername(username);
            verificator.setVerificationType("email");
    
            user.setEmail(email);
            user.setEnabled(false);
    
            usuarioDao.save(user);
            verificationRecords.save(verificator);
    
             try {
                String url = "http://localhost:8080/verify-account/".concat(token);
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("name", user.getNombre());
                model.put("token", token);
                model.put("link", url);
                emailService.sendMessageUsingThymeleafTemplate(user.getEmail(), "Verificacion de correo", "email-verificator", model);
                return true;
            } catch (MessagingException e) {
                System.out.println("ERRROR AL ENVIAR EMAIL.");
                return false;
            }
        }
        return false;
    }

}
