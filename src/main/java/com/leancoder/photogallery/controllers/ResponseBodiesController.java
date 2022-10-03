package com.leancoder.photogallery.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    // Endpoint que genera un registro de like para una foto en especifico, por parte de un usuario especifico:
    @PostMapping("/like")
    public ResponseEntity<Object> GenerarLike(@RequestBody(required = true) RestRequestValidator likeValidator) {
        var res = photoService.likearFoto(likeValidator.getPhoto_id(), likeValidator.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // Endpoint que quita un registro de like para una foto en especifico, por parte de un usuario especifico (si tiene un like para esa foto registrado):
    @PostMapping("/dislike")
    public ResponseEntity<Object> GenerarDisLike(@RequestBody(required = true) RestRequestValidator likeValidator) {
        var res = photoService.quitarLike(likeValidator.getPhoto_id(), likeValidator.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // Endpoint que genera un registro de un favorito para una foto en especifico, por parte de un usuario especifico:
    @PostMapping("/add-favorites")
    public ResponseEntity<Object> AñadirFavoritos(@RequestBody(required = true) RestRequestValidator validator) {
        var res = photoService.añadirFavoritos(validator.getPhoto_id(), validator.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // Endpoint que quita un registro de un favorito para una foto en especifico, por parte de un usuario especifico (si tiene un favorito para esa foto registrado):
    @PostMapping("/remove-favorites")
    public ResponseEntity<Object> QuitarFavoritos(@RequestBody(required = true) RestRequestValidator validator) {
        var res = photoService.quitarFavoritos(validator.getPhoto_id(), validator.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
