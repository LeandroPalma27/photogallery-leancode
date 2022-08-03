package com.leancoder.photogallery.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.leancoder.photogallery.models.domains.responses.RestRequestResponse;
import com.leancoder.photogallery.models.domains.validators.RestRequestValidator;
import com.leancoder.photogallery.models.services.photo.interfaces.IPhotoService;
import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;
/* 
 * Controlador para la funcionalidades manejadas como un @RestController
 * Aca se encuentran los metodos que retornan datos JSON, mas no vistas.
 */
@RestController
public class ResponseBodiesController {

    @Autowired
    IUsuarioService usuarioService;

    @Autowired
    IPhotoService photoService;

    @PostMapping("/like")
    public ResponseEntity<Object> GenerarLike(@RequestBody(required = true) RestRequestValidator likeValidator) {
        var res = photoService.likearFoto(likeValidator.getPhoto_id(), likeValidator.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/dislike")
    public ResponseEntity<Object> GenerarDisLike(@RequestBody(required = true) RestRequestValidator likeValidator) {
        var res = photoService.quitarLike(likeValidator.getPhoto_id(), likeValidator.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/add-favorites")
    public ResponseEntity<Object> AñadirFavoritos(@RequestBody(required = true) RestRequestValidator validator) {
        var res = photoService.añadirFavoritos(validator.getPhoto_id(), validator.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/remove-favorites")
    public ResponseEntity<Object> QuitarFavoritos(@RequestBody(required = true) RestRequestValidator validator) {
        var res = photoService.quitarFavoritos(validator.getPhoto_id(), validator.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
