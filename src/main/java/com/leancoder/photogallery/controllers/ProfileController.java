package com.leancoder.photogallery.controllers;

import com.leancoder.photogallery.models.service.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Secured(value={"ROLE_USER", "ROLE_ADMIN"})
@RequestMapping("/account")
public class ProfileController {

    @Autowired
    IUsuarioService usuarioService;
    
    @GetMapping({"/", "", "/{username}"})
    public String Profile(@PathVariable(name="username", required=false) String username, Authentication authentication, Model model) {
        
        var usuario = usuarioService.obtenerUsuarioPorUsername(username);
        
        if(usuario != null && usuario.getUsername().equals(authentication.getName()) ) {
            
            model.addAttribute("title", "Profile");
            model.addAttribute("usuario", usuario);
            model.addAttribute("user", usuario.getUsername());
            model.addAttribute("nombre", usuario.getNombre());

            return "profile";
        } 

        return "redirect:/";

    }

    @GetMapping("/update/{username}")
    public String Edit(@PathVariable(name="username") String username, Authentication authentication, Model model) {
        
        var usuario = usuarioService.obtenerUsuarioPorUsername(username);
        
        if (usuario!=null && usuario.getUsername().equals(authentication.getName())) {
            model.addAttribute("title", "Update account");
            model.addAttribute("usuario", usuario);
            model.addAttribute("user", usuario.getUsername());
            model.addAttribute("nombre", usuario.getNombre());
            return "edit_account";
        }

        return "redirect:/";

    }

    @GetMapping("/delete/{username}")
    public String Delete(@PathVariable("username") String username, Model model) {
        return "redirect:/login";
    }

}
