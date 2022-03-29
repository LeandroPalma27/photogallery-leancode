package com.leancoder.photogallery.controllers;

import java.security.Principal;

import javax.validation.Valid;

import com.leancoder.photogallery.models.domain.FailRegister;
import com.leancoder.photogallery.models.domain.RegisterUsuario;
import com.leancoder.photogallery.models.service.IUsuarioService;

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
    public String register(RegisterUsuario usuario, Principal principal, RedirectAttributes flash, Model model) {
        // Principal sirve para verificar si ya hay una sesion existente:
        if (principal != null) {
            flash.addAttribute("info", "Hay una sesion activa, cierre sesion e intentelo de nuevo.");
            return "redirect:/";
        }
        model.addAttribute("usuarioDomain", usuario);
        return "register";
    }

    @PostMapping("/signup")
    public String processRegister(@Validated @ModelAttribute("usuarioDomain") RegisterUsuario usuario, BindingResult result, Model model) {

        if (result.hasErrors()) {
            var errores = result.getFieldErrors();
            for (var error: errores) {
                if (error.getField().equals("password")) {
                    if (error.getDefaultMessage().length() > 30) {
                        model.addAttribute("passwordError", "La contraseña debe tener entre 8 y 30 caracteres; un digito, una letra mayuscula y un caracter especial(como minimo).");
                    } else {
                        model.addAttribute("passwordError", error.getDefaultMessage());
                    }
                }
            }
            return "register";
        }

        if (!usuario.getPassword().equals(usuario.getConfirmPassword())) {
            model.addAttribute("message", "Las contraseñas no coinciden.");
            return "register";
        }

        FailRegister res = (FailRegister) usuarioService.registrarUsuario(usuario);

        if (res.getNameError().equals("duplicated_username")) {
            model.addAttribute("error", res.getMessage());
            return "register";
        }

        if (res.getNameError().equals("duplicated_email")) {
            model.addAttribute("error", res.getMessage());
            return "register";
        }


        return "redirect:/login";
    }

}
