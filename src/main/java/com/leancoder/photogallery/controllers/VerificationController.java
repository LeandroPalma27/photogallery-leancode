package com.leancoder.photogallery.controllers;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.leancoder.photogallery.models.dao.IVerificationRecords;
import com.leancoder.photogallery.models.domains.validators.ForgotPasswordValidator;
import com.leancoder.photogallery.models.entities.verification.VerificationRecords;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

@Controller
// Controlador diseñado para la verificacion de peticiones para cambios de contraseña o verificaion de cuentas
// Requerimos guardar el objeto que contiene el token para poder redireccionar a la ruta que carge el formulario para el cambio de contraseña
@SessionAttributes({"verificator"})
public class VerificationController {

    @Autowired
    IUsuarioService usuarioService;

    @Autowired
    IVerificationRecords verificationRecords;

    @ModelAttribute("profilePictureUser")
    public String cargarFotoDePerfil(Authentication authentication) {

        if (authentication != null) {
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

            var fotosUsuario = usuario.getPhotos();
            for (var foto : fotosUsuario) {
                if (foto.getRoles().size() > 1) {
                    for (var role : foto.getRoles()) {
                        if (role.getRole().equals("ROLE_PROFILE")) {
                            return foto.getUrlPhoto();
                        }
                    }
                }
            }
        }
        return null;

    }

    // Para la verificacion de cuenta, solo hace falta entrar a la ruta con el token correspondiente, eso automaticamente verificara la cuenta y cambiara el estado de la peticion en la bd
    @GetMapping("/verify-account/{token}")
    public String VerificarEmail(@PathVariable("token") String token, Authentication authentication, Model model) {
        model.addAttribute("title", "Verificar cuenta");
        if (token == null || token == "") {
            return "redirect:/";
        }
        var res = usuarioService.VerificarUsuario(token);
        if (authentication != null) {
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
            model.addAttribute("usuario", usuario);
        }
        if (res.getMessage()!=null) {
            model.addAttribute("verified", true);
            return "verify-email";
        } 
        model.addAttribute("verified", false);
        return "verify-email";

    }

    @GetMapping("/change-password/{token}")
    public String CambiarContraseña(@PathVariable("token") String token, Principal principal, RedirectAttributes flash, Model model) {
        model.addAttribute("title", "Cambiar contraseña");
        if (token == null || token == "") {
            return "redirect:/";
        }
        if (principal != null) {
            flash.addFlashAttribute("info", "Ya se ha iniciado sesion.");
            return "redirect:/";
        }
        // Buscamos la peticion en la base de datos, y verificamos que este activa y que sea de tipo change_password
        var verificator = verificationRecords.findByToken(token);
        var verificadorExiste = verificator != null && verificator.getVerificationType().equals("change_password") && verificator.getEnabled() == true ? true : false;
        if (!verificadorExiste) {
            // Caso contrario
            flash.addFlashAttribute("info", "El codigo de verificacion no existe o esta caducado.");
            return "redirect:/";
        }
        // Si procede, cargamos el objeto de formulario correspondiente, asi como su vista
        ForgotPasswordValidator validator = new ForgotPasswordValidator();
        model.addAttribute("validator", validator);
        model.addAttribute("verificator", verificator);
        return "forgot-password";
    }

    // Ruta para el proceso de cambio de contraseña
    @PostMapping("/change-password")
    public String ProcesarCambioContraseña(@ModelAttribute("verificator") VerificationRecords verificator, @Valid ForgotPasswordValidator validator, BindingResult result, RedirectAttributes flash, SessionStatus status, Model model) {
        // En este post, requerimos el objeto del token, asi que de la sesion sacamos dicho objeto para hacer redirecciones si es necesario.
        if (result.hasErrors() || !validator.getNewPassword().equals(validator.getConfirmPassword())) {
            flash.addFlashAttribute("error_changePassword", "Recuerde que la nueva contraseña debe tener una letra mayuscula, un caracter especial y tambien un numero. Asi como tambien coincidir con el campo de confirmacion de contraseña.");
            return "redirect:/change-password/".concat(verificator.getToken());
        }
        // Si se procesa, se carga un true:
        var res = usuarioService.cambiarContraseñaOlvidada(verificator, validator.getNewPassword());
        if (!res) {
            flash.addFlashAttribute("error_changePassword", "Ocurrio algun error, vuelva a intentarlo mas tarde o solicite otro cambio de contraseña.");
            return "redirect:/change-password/".concat(verificator.getToken());
        }
        // Si carga con exito, se limpia la sesion
        status.setComplete();
        flash.addFlashAttribute("info", "Se realizo el cambio de contraseña con exito.");
        return "redirect:/login";
    }

    // Endpoint para solicitar otra verificacion (para en caso de que existan problemas con el correo envio para la verificacion)
    @GetMapping("/reapply-verification")
    public String VolverSolicitarVerificacion(Authentication authentication, RedirectAttributes flash) {
        if (authentication != null) {
            // Necesitamos que un usuario este logueado
            var res = usuarioService.solicitarVerificacionCuenta(authentication.getName());
            // Cargamos flash y redireccion correspondiente:
            if (res) {
                flash.addFlashAttribute("successMessage", "Solicitud procesada con exito, revise su correo y verifique su cuenta.");
                return "redirect:/account";
            }
            flash.addFlashAttribute("errorMessage", "Ocurrio algun error al solicitar la verificacion, intentelo mas tarde.");
            return "redirect:/account";
        }
        return "redirect:/";
    }

}
