package com.leancoder.photogallery.controllers;

import com.leancoder.photogallery.models.dao.IUsuarioDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    IUsuarioDao usuarioDao;
    
    @GetMapping({"", "/"})
    public String Home(Authentication authentication, Model model) {
        model.addAttribute("title", "Photogallery");
        
        if (authentication != null) {

            var usuario = usuarioDao.findByUsername(authentication.getName());

            model.addAttribute("user", usuario.getUsername());
            model.addAttribute("nombre", usuario.getNombre());
        }


        return "index";
    }

}
