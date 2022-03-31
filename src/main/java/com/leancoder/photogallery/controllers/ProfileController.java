package com.leancoder.photogallery.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.leancoder.photogallery.auth.handler.CloseManualSession;
import com.leancoder.photogallery.models.entity.Usuario;
import com.leancoder.photogallery.models.service.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@Secured(value = { "ROLE_USER", "ROLE_ADMIN" })
@RequestMapping("/account")
@SessionAttributes({ "usuario" })
public class ProfileController {

    @Autowired
    IUsuarioService usuarioService;

    /*
     * @ModelAttribute("usuario")
     * public Usuario cargarUsuario(Authentication authentication) {
     * return usuarioService.obtenerUsuarioPorUsername(authentication.getName());
     * }
     */

    @GetMapping({ "/", "", "/{username}" })
    public String Profile(@PathVariable(name = "username", required = false) String username,
            Authentication authentication, Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(username);

        if (usuario != null && usuario.getUsername().equals(authentication.getName())) {

            model.addAttribute("title", "Profile");
            model.addAttribute("usuario", usuario);
            model.addAttribute("user", usuario.getUsername());
            model.addAttribute("nombre", usuario.getNombre());

            return "profile";
        }

        return "redirect:/";

    }

    @GetMapping("/update")
    public String Edit(Authentication authentication, Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());

        model.addAttribute("title", "Update account");
        model.addAttribute("usuario", usuario);
        model.addAttribute("user", usuario.getUsername());
        model.addAttribute("nombre", usuario.getNombre());
        return "edit_account";

    }

    @GetMapping("/delete")
    public String Delete(HttpServletRequest request, HttpServletResponse response, Authentication authentication, Model model) {

        var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
        usuarioService.eliminarUsuario(usuario);

        CloseManualSession sesion = new CloseManualSession();
        sesion.logout(request, response, authentication);

        return "redirect:/login";
    }

}
