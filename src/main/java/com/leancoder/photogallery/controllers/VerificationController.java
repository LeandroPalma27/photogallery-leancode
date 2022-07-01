package com.leancoder.photogallery.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

@Controller
public class VerificationController {

    @Autowired
    IUsuarioService usuarioService;

    @GetMapping("/verify-account/{token}")
    public String VerificarEmail(@PathVariable("token") String token, Model model) {
        model.addAttribute("title", "Verificator account");
        if (token == null || token == "") {
            return "redirect:/";
        }
        var res = usuarioService.VerificarUsuario(token);
        if (res.getMessage()!=null) {
            model.addAttribute("verified", true);
            return "verify-email";
        } 
        model.addAttribute("verified", false);
        return "verify-email";

    }

}
