package com.leancoder.photogallery.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
    <=== Controlador para el manejo de las vistas personzalidas para el login ===>
        * Con este controlador podemos manejar la carga de una vista personzalida para el LOGIN.   
 */
@Controller
public class LoginController {
    
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

}
