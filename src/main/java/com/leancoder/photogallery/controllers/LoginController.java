package com.leancoder.photogallery.controllers;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.leancoder.photogallery.models.domains.validators.EnterEmailValidator;
import com.leancoder.photogallery.models.services.user.UsuarioService;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

/*
    <=== Controlador para el manejo de las vistas personzalidas para el login ===>
        * Con este controlador podemos manejar la carga de una vista personzalida para el LOGIN.   
 */
@Controller
public class LoginController {

    @Autowired
    IUsuarioService usuarioService;
    
    /*
        Endpoint que carga la vista personalizada para el LOGIN en nuestra aplicacion.
     */
    @GetMapping("/login")
    public String login(@RequestParam(value="error", required = false) String error, Model model, Principal principal, RedirectAttributes flash) {
        
        // El objeto principal carga la representacion de un usuario (si esta logueado y tiene una sesion valida) con toda
        // su informacion.
        if (principal != null) {
            flash.addFlashAttribute("info", "Ya se ha iniciado sesion.");
            return "redirect:/";
        }

        // Al producirse un error, internamente se envia un parametro con informacion, en la url. 
        // Este parametro se carga como "error"
        if (error != null) {
            model.addAttribute("post_register", "Remember to verify your account, check your email and verify it through the verification link.");
            model.addAttribute("errorLogin", "There is no account with the credentials placed or it may not be verified.");
        }

        return "login";
    }

    @GetMapping("/forgot-password")
    public String IntroducirEmailParaCambiarContraseña(Model model, Principal principal, RedirectAttributes flash) {
        if (principal != null) {
            flash.addFlashAttribute("info", "Ya se ha iniciado sesion.");
            return "redirect:/";
        }
        EnterEmailValidator validator = new EnterEmailValidator();
        model.addAttribute("title", "Enviar email");
        model.addAttribute("validator", validator);
        return "enter-email";
    }

    @PostMapping("/forgot-password")
    public String CrearPeticionParaCambioDeContraseña(@Valid EnterEmailValidator validator, BindingResult result, RedirectAttributes flash, Model model) {
        if (result.hasErrors()) {
            flash.addFlashAttribute("error_enterEmail", "Ingrese una direccion de correo electronico valida.");
            return "redirect:/forgot-password";
        }

        var res = usuarioService.crearPeticionParaCambioContraseña(validator.getEmail());
        if (res == null) {
            flash.addFlashAttribute("error_enterEmail", "La direccion de correo ingresada no existe o no esta verificada en el sistema.");
            return "redirect:/forgot-password";
        }
        flash.addFlashAttribute("info", "Enviamos la solicitud de cambio de contraseña a la direccion de correo ingresada, revise la bandeja de entrada y siga los pasos indicados.");
        return "redirect:/login";
    }

}
