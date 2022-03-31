package com.leancoder.photogallery.models.service;

import com.leancoder.photogallery.models.domain.RegisterUsuario;
import com.leancoder.photogallery.models.entity.Usuario;

public interface IUsuarioService {
    
    public Usuario registrarUsuario(RegisterUsuario usuario);

    public Usuario obtenerUsuarioPorUsername(String username);

    public void eliminarUsuario(Usuario usuario);

}
