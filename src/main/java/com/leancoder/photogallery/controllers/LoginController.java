package com.leancoder.photogallery.controllers;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.leancoder.photogallery.models.domains.validators.EnterEmailValidator;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

/*
    <=== Controlador para el manejo de las vistas personzalidas para el login ===>
        * Con este controlador podemos manejar la carga de una vista personzalida para el LOGIN.   
 */
@Controller
public class LoginController {

    // Instancia service para la entidad usuario
    @Autowired
    IUsuarioService usuarioService;
    
    /*
        Endpoint que carga la vista personalizada para el LOGIN en nuestra aplicacion.
     */
    @GetMapping("/login")
    // Aca inyectamos la interfaz Principal con la misma intencion en donde inyectamos Authentication, la idea es poder validar si hay un usuario logueado
    public String login(@RequestParam(value="error", required = false) String error, Model model, Principal principal, RedirectAttributes flash) {
        
        // El objeto principal carga la representacion de un usuario (si esta logueado y tiene una sesion valida) con toda
        // su informacion.
        if (principal != null) {
            flash.addFlashAttribute("info", "Ya se ha iniciado sesion.");
            return "redirect:/";
        }

        // Al producirse un error, se redirige a la misma url, pero con un parametro llamado "error". 
        if (error != null) {
            model.addAttribute("errorLogin", "No se encontro una cuenta con los credenciales ingresados.");
        }

        return "login";
    }

    // Vista para el formulario de cambio de contraseña
    @GetMapping("/forgot-password")
    // Inyecte RedirectAttributes para a traves de un flash mostrar un mensaje luego de redirigir a la lista home.
    public String IntroducirEmailParaCambiarContraseña(Model model, Principal principal, RedirectAttributes flash) {
        if (principal != null) {
            flash.addFlashAttribute("info", "Ya se ha iniciado sesion.");
            return "redirect:/";
        }
        // Caso contrario, se cargara el formulario con su objeto respectivo
        EnterEmailValidator validator = new EnterEmailValidator();
        model.addAttribute("title", "Enviar email");
        model.addAttribute("validator", validator);
        return "enter-email";
    }

    // Proceso del formulario para cambio de contraseña
    @PostMapping("/forgot-password")
    public String CrearPeticionParaCambioDeContraseña(@Valid EnterEmailValidator validator, BindingResult result, RedirectAttributes flash, Model model) {
        // Validamos que el formulario se complete de forma correcta, caso contrario se redirige a su vista con el unico mensaje de error posible
        if (result.hasErrors()) {
            flash.addFlashAttribute("error_enterEmail", "Ingrese una direccion de correo electronico valida.");
            return "redirect:/forgot-password";
        }
        // Caso contrario creamos un registro de peticion para cambio de contraseña, con su token, y en funcion a la respuesta del service redirigimos con sus respectivos mensajes
        var res = usuarioService.crearPeticionParaCambioContraseña(validator.getEmail());
        if (res == null) {
            flash.addFlashAttribute("error_enterEmail", "La direccion de correo ingresada no existe o no esta verificada en el sistema.");
            return "redirect:/forgot-password";
        }
        flash.addFlashAttribute("info", "Enviamos la solicitud de cambio de contraseña a la direccion de correo ingresada, revise la bandeja de entrada y siga los pasos indicados.");
        return "redirect:/login";
    }

}
