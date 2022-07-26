package com.leancoder.photogallery.controllers;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.leancoder.photogallery.models.dao.IVerificationRecords;
import com.leancoder.photogallery.models.domains.validators.ForgotPasswordValidator;
import com.leancoder.photogallery.models.entities.verification.VerificationRecords;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

@Controller
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

    @ModelAttribute("profilePictureUploadId")
    public String cargarIdDeFotoPerfil(Authentication authentication) {

        if (authentication != null) {
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

            var fotosUsuario = usuario.getPhotos();
            for (var foto : fotosUsuario) {
                if (foto.getRoles().size() > 1) {
                    for (var role : foto.getRoles()) {
                        if (role.getRole().equals("ROLE_PROFILE")) {
                            return foto.getUploadId();
                        }
                    }
                }
            }
        }
        return null;

    }

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
        var verificator = verificationRecords.findByToken(token);
        var verificadorExiste = verificator != null && verificator.getVerificationType().equals("change_password") && verificator.getEnabled() == true ? true : false;
        if (!verificadorExiste) {
            flash.addFlashAttribute("info", "El codigo de verificacion no existe o esta caducado.");
            return "redirect:/";
        }
        ForgotPasswordValidator validator = new ForgotPasswordValidator();
        model.addAttribute("validator", validator);
        model.addAttribute("verificator", verificator);
        return "forgot-password";
    }

    @PostMapping("/change-password")
    public String ProcesarCambioContraseña(@ModelAttribute("verificator") VerificationRecords verificator, @Valid ForgotPasswordValidator validator, BindingResult result, RedirectAttributes flash, SessionStatus status, Model model) {
        if (result.hasErrors() || !validator.getNewPassword().equals(validator.getConfirmPassword())) {
            flash.addFlashAttribute("error_changePassword", "Recuerde que la nueva contraseña debe tener una letra mayuscula, un caracter especial y tambien un numero. Asi como tambien coincidir con el campo de confirmacion de contraseña.");
            return "redirect:/change-password/".concat(verificator.getToken());
        }
        var res = usuarioService.cambiarContraseñaOlvidada(verificator, validator.getNewPassword());
        if (!res) {
            flash.addFlashAttribute("error_changePassword", "Ocurrio algun error, vuelva a intentarlo mas tarde o solicite otro cambio de contraseña.");
            return "redirect:/change-password/".concat(verificator.getToken());
        }
        status.setComplete();
        flash.addFlashAttribute("info", "Se realizo el cambio de contraseña con exito.");
        return "redirect:/login";
    }

    @GetMapping("/reapply-verification")
    public String VolverSolicitarVerificacion(Authentication authentication, RedirectAttributes flash) {
        if (authentication != null) {
            var res = usuarioService.solicitarVerificacionCuenta(authentication.getName());
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
