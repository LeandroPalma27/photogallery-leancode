package com.leancoder.photogallery.controllers;

import java.security.Principal;

import javax.validation.Valid;

import com.leancoder.photogallery.models.domains.validators.UserRegisterDomainValidator;
import com.leancoder.photogallery.models.entities.user.User;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {
    
    @Autowired
    IUsuarioService usuarioService;
    
    @GetMapping("/signup")
    public String register(UserRegisterDomainValidator usuario, Principal principal, RedirectAttributes flash, Model model) {
        // Principal sirve para verificar si ya hay una sesion existente:
        if (principal != null) {
            flash.addFlashAttribute("info", "Hay una sesion activa, cierre sesion e intentelo de nuevo.");
            return "redirect:/";
        }
        model.addAttribute("title", "Register");
        model.addAttribute("usuarioDomain", usuario);
        model.addAttribute("generos", usuarioService.listarGenerosUsuario());
        return "register";
    }

    @PostMapping("/signup")
    public String processRegister(@Validated @ModelAttribute("usuarioDomain") UserRegisterDomainValidator usuario, RedirectAttributes flash, BindingResult result, Model model) {

        model.addAttribute("generos", usuarioService.listarGenerosUsuario());

        if (result.hasErrors()) {
            var errores = result.getFieldErrors();
            for (var error: errores) {
                if (error.getField().equals("password")) {
                    // El unico mensaje que supera los 30 caracteres el de error por contraseña no valida
                    if (error.getDefaultMessage().length() > 30) {
                        model.addAttribute("passwordAdvancedError", "La contraseña debe tener entre 8 y 30 caracteres; un digito, una letra mayuscula y un caracter especial(como minimo).");
                    } else {
                        // Y si no es ese, es el mensaje de que se debe rellenar el campo
                        model.addAttribute("passwordNormalError", error.getDefaultMessage());
                    }
                }
            }
            return "register";
        }

        // Por si es que las contraseñas ingresadas no coinciden:
        if (!usuario.getPassword().equals(usuario.getConfirmPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }

        var res = usuarioService.registrarUsuario(usuario);

        // Es porque si hubo algun error ya que existe algun nombre de error:
        if (res.getName() != null) {
            model.addAttribute("error", res.getMessage());
            return "register";
        }

        flash.addFlashAttribute("post_register", "An email was sent to your email address, please verify your account to log in.");
        return "redirect:/login";
    }

}
