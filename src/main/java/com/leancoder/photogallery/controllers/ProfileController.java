package com.leancoder.photogallery.controllers;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.leancoder.photogallery.custom.close_session.CloseManualSession;
import com.leancoder.photogallery.models.domains.validators.EnterEmailValidator;
import com.leancoder.photogallery.models.domains.validators.NameAndLastNameOfUserValidator;
import com.leancoder.photogallery.models.domains.validators.OneDetaiOflUserValidator;
import com.leancoder.photogallery.models.domains.validators.PasswordUserValidator;
import com.leancoder.photogallery.models.domains.validators.PhotoUploaderValidator;
import com.leancoder.photogallery.models.entities.photo.Photo;
import com.leancoder.photogallery.models.entities.user.User;
import com.leancoder.photogallery.models.services.photo.interfaces.IPhotoService;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
    <=== Controlador para la carga de las vistas que cargue informacion de un usuario en un perfil.  ===>
        * Con este controlador podemos manejar la carga de vistas dcon informacion de un usuario.   
        * Ademas de eso tambien se gestionan los procesos y transacciones para la actualizacion de los detalles del usuario.
        * Tambien se permite poder acceder a otros dao para poder cambiar la foto de perfil del usuario
        * Desde aca se permite cambiar la constraseña del usuario, sin necesidad de una verificacion con un correo electronico
        * Actualmente no se permite cambiar el correo del usuario(proximamente se pedira verificar el correo).
        * Esta pendiente una funcionalidad que al registrara un usuario la cuenta no estara activada hasta que se verifique el correo
        * El cambio de contraseña cuando se olvida se hara con una verificacion con el correo
 */
@Controller
@RequestMapping("/account")
public class ProfileController {

    @Autowired
    IUsuarioService usuarioService;

    @Autowired
    CloseManualSession closeManualSession;

    @Autowired
    IPhotoService photoService;

    // Objetos creados para un valor por defecto en los formularios o vistas (el
    // objeto que carga al usuario):
    @ModelAttribute("photoValidator")
    public PhotoUploaderValidator validator() {
        return new PhotoUploaderValidator();
    }

    /*
        Objecto cargado con la descripcion del usuario para la vista que actualiza ese campo en su formulario respectivo.
     */
    @ModelAttribute("formDescripcion")
    public OneDetaiOflUserValidator cargarDescripcion(Authentication authentication) {
        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        OneDetaiOflUserValidator u = new OneDetaiOflUserValidator();
        u.setContent(usuario.getDescription());
        return u;
    }

    /*
        Objecto cargado con el username para la vista que actualiza ese campo en su formulario respectivo.
     */
    @ModelAttribute("formUsername")
    public OneDetaiOflUserValidator cargarUsername(Authentication authentication) {
        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        OneDetaiOflUserValidator u = new OneDetaiOflUserValidator();
        u.setContent(usuario.getUsername());
        return u;
    }

    /*
        Objecto cargado con los nombres del usuario para la vista que actualiza ese campo en su formulario respectivo.
     */
    @ModelAttribute("formNameAndLastName")
    public NameAndLastNameOfUserValidator cargarNombreAndApellidos(Authentication authentication) {
        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        NameAndLastNameOfUserValidator updateNameAndLastName = new NameAndLastNameOfUserValidator();
        updateNameAndLastName.setName(usuario.getNombre());
        updateNameAndLastName.setLastname(usuario.getApellidos());
        return updateNameAndLastName;
    }

    /*
        Objecto cargado con informacion general del usuario globalmente para todas las vistas en este controlador.
     */
    @ModelAttribute("usuario")
    public User cargarUsuario(Authentication authentication) {
        return usuarioService.obtenerUsuarioPorUsername(authentication.getName());
    }

    /*
        Objecto cargado con la contraseña del usuario para la vista que actualiza ese campo en su formulario respectivo.
     */
    @ModelAttribute("formChangePassword")
    public PasswordUserValidator cargarPasswordUpdater() {
        PasswordUserValidator pv = new PasswordUserValidator();
        pv.setOldpass("");
        pv.setNewpass("");
        return pv;
    }

    @ModelAttribute("formChangeEmail")
    public EnterEmailValidator cargarEmailUpdater() {
        EnterEmailValidator ev = new EnterEmailValidator();
        ev.setEmail("");
        return ev;
    }

    /*
        Endpoint que carga la la vista del perfil del usuario.
     */
    @GetMapping({ "/", "" })
    public String Profile(Authentication authentication, Model model) {

        // 200 CARACTERES COMO MUCHO EN LA DESCRIPCION
        PhotoUploaderValidator validator = new PhotoUploaderValidator();
        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

        var fotosUsuario = usuario.getPhotos();
        Photo fotoPerfil = null;

        principal: for (var foto : fotosUsuario) {
            if (foto.getRoles().size() > 1) {
                for (var role : foto.getRoles()) {
                    if (role.getRole().equals("ROLE_PROFILE")) {
                        fotoPerfil = foto;
                        break principal;
                    }
                }
            } else {
                continue;
            }
        }

        if (fotoPerfil != null) {
            model.addAttribute("profilePictureUser", fotoPerfil.getUrlPhoto());
            model.addAttribute("profilePictureUploadId", fotoPerfil.getUploadId());
        }

        model.addAttribute("title", "Profile");
        model.addAttribute("usuario", usuario);
        model.addAttribute("photoValidator", validator);

        return "profile";

    }

    /*
        Endpoint que procesa el formulario que actualiza la descripcion.
     */
    @PostMapping("/update-description")
    public String EditDescription(@Valid @ModelAttribute("formDescripcion") OneDetaiOflUserValidator detail,
            BindingResult result, Authentication authentication, Model model) {

        model.addAttribute("title", "Profile");

        if (result.hasErrors()) {
            model.addAttribute("modalActivator", "descrip");
            return "profile";
        }

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

        usuarioService.actualizarDescripcion(usuario, detail.getContent());

        return "redirect:/account";

    }

    /*
        Endpoint que procesa el formulario que actualiza los nombres y apellidos del usuario.
     */
    @PostMapping("/update-names")
    public String EditNameAndLastName(
            @Valid @ModelAttribute("formNameAndLastName") NameAndLastNameOfUserValidator detail,
            BindingResult result, Authentication authentication, Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        model.addAttribute("title", "Profile");

        if (result.hasErrors()) {
            model.addAttribute("modalActivator", "nameAndLastName");
            return "profile";
        }

        usuarioService.actualizarNombreAndApellidos(usuario, detail);

        return "redirect:/account";

    }

    /*
        Endpoint que procesa el formulario que actualiza el username.
     */
    @PostMapping("/update-username")
    public String EditUsername(@Valid @ModelAttribute("formUsername") OneDetaiOflUserValidator detail,
            BindingResult result, Authentication authentication, RedirectAttributes flash, Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        model.addAttribute("title", "Profile");

        if (result.hasErrors()) {
            model.addAttribute("modalActivator", "password");
            return "profile";
        }

        var res = usuarioService.actualizarUsername(usuario, detail.getContent().trim());
        if (res.getMessage() != null) {
            flash.addFlashAttribute("errorMessage", res.getMessage());
            return "redirect:/account";
        }

        // Para actualizar la informacion del usuario logueado que esta en la sesion
        // activa:
        UsernamePasswordAuthenticationToken authenticationUpdated = new UsernamePasswordAuthenticationToken(
                detail.getContent(), usuario.getPassword(),
                authentication.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationUpdated);

        flash.addFlashAttribute("successMessage", "Se cambio con exito el username.");

        return "redirect:/account";

    }

    /*
        Endpoint que procesa el formulario que actualiza la contraseña.
     */
    @PostMapping("/update-pass1")
    public String ChangePasswordFromLoggedAccount(
            @Valid @ModelAttribute("formChangePassword") PasswordUserValidator detailPassword,
            BindingResult result, Authentication authentication, RedirectAttributes flash, Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        model.addAttribute("title", "Profile");

        if (detailPassword.getOldpass() == "") {
            flash.addFlashAttribute("errorMessage", "No deje nigun campo vacío.");
            return "redirect:/account";
        }

        if (result.hasErrors()) {
            flash.addFlashAttribute("errorMessage",
                    "La nueva contraseña debe tener: Entre 8 y 30 caracteres, un digito, una letra mayuscula y un caracter especial(como minimo).");
            return "redirect:/account";
        }

        var res = usuarioService.actualizarConstraseña(detailPassword, usuario);

        if (res.getName() != null) {

            flash.addFlashAttribute("errorMessage", res.getMessage());

            return "redirect:/account";
        }

        flash.addFlashAttribute("successMessage", "La contraseña se cambio con exito");

        UsernamePasswordAuthenticationToken authenticationUpdated = new UsernamePasswordAuthenticationToken(
                usuario.getUsername(), res.getMessage(),
                authentication.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationUpdated);

        return "redirect:/account";

    }

    /*
        Endpoint que elmina a un usuario.
     */
    @GetMapping("/delete")
    public String Delete(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
            Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        usuarioService.eliminarUsuario(usuario);

        closeManualSession.logout(request, response, authentication);

        return "redirect:/login";
    }

    /*
        Endpoint que procesa el formulario que establece una foto de perfil para el usuario.
     */
    @PostMapping("/upload-profile-photo")
    public String UploadProfilePhoto(@Valid @ModelAttribute("photoValidator") PhotoUploaderValidator validator,
            BindingResult result, Authentication authentication, RedirectAttributes flash, Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        model.addAttribute("title", "Profile");

        var fotosUsuario = usuario.getPhotos();
        principal: for (var foto : fotosUsuario) {
            if (foto.getRoles().size() > 1) {
                for (var role : foto.getRoles()) {
                    if (role.getRole().equals("ROLE_PROFILE")) {
                        model.addAttribute("profilePictureUser", foto.getUrlPhoto());
                        model.addAttribute("profilePictureUploadId", foto.getUploadId());
                        break principal;
                    }
                }
            } else {
                continue;
            }
        }

        if (result.hasErrors()) {

            if (validator.getFile().isEmpty()) {
                ObjectError fileError = new ObjectError("fileError", "Select some picture.");
                var errorsUpdated = new ArrayList<ObjectError>();
                result.getAllErrors().forEach((error) -> {
                    errorsUpdated.add(error);
                });
                errorsUpdated.add(fileError);
                model.addAttribute("modalActivator", "profilePicture");
                model.addAttribute("formErrors", errorsUpdated);
                return "profile";
            }

            model.addAttribute("modalActivator", "profilePicture");
            model.addAttribute("formErrors", result.getAllErrors());
            return "profile";
        }

        if (validator.getFile().isEmpty()) {
            ObjectError fileError = new ObjectError("fileError", "Select some picture.");
            var errorsUpdated = new ArrayList<ObjectError>();
            errorsUpdated.add(fileError);
            model.addAttribute("modalActivator", "profilePicture");
            model.addAttribute("formErrors", errorsUpdated);
            return "profile";
        }

        var res = photoService.registrarFotoPerfil(validator, usuario);

        if (res.getName().equals("unknown_error")) {
            flash.addFlashAttribute("errorMessage", res.getMessage());
            return "redirect:/account";
        }

        flash.addFlashAttribute("successMessage", "Registro de foto exitoso.");

        return "redirect:/account";
    }

    /*
        Endpoint que elimina la foto de perfi del usuario.
     */
    @GetMapping("/delete/profile-picture/{public_id}")
    public String DeleteProfilePicture(@PathVariable("public_id") String public_id, RedirectAttributes flash,
            Model model) throws IOException {

        var res = photoService.removerFotoDePerfil(public_id);

        if (res.getName().equals("successful_remove")) {
            flash.addFlashAttribute("successMessage", res.getMessage());
            return "redirect:/account";
        } else {
            flash.addFlashAttribute("errorMessage", res.getMessage());
            return "redirect:/account";
        }

    }

    @PostMapping("/update/email")
    public String UpdateEmail(@Valid @ModelAttribute("formChangeEmail") EnterEmailValidator validator, BindingResult result, Authentication authentication, RedirectAttributes flash, Model model) {
        if (result.hasErrors()) {
            flash.addFlashAttribute("errorMessage", "Ingrese un email valido");
            return "redirect:/account";
        }
        var res = usuarioService.actualizarEmail(validator.getEmail(), authentication.getName());
        if (res) {
            flash.addFlashAttribute("successMessage", "Se realizo el cambio de email, por favor revise su correo para poder verificar su cuenta.");
            return "redirect:/account";
        }
        flash.addFlashAttribute("errorMessage", "Ocurrio algun error, intentelo de nuevo.");
        return "redirect:/account";
    }

}
