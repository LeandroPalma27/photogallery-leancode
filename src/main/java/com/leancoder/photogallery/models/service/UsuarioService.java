package com.leancoder.photogallery.models.service;

import com.leancoder.photogallery.models.dao.IRoleDao;
import com.leancoder.photogallery.models.dao.IUsuarioDao;
import com.leancoder.photogallery.models.domain.FailRegister;
import com.leancoder.photogallery.models.domain.RegisterUsuario;
import com.leancoder.photogallery.models.entity.Role;
import com.leancoder.photogallery.models.entity.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    IUsuarioDao usuarioDao;

    @Autowired
    IRoleDao roleDao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Usuario registrarUsuario(RegisterUsuario usuario) {

        if (usuarioDao.findByUsername(usuario.getUsername()) == null) {

            if (usuarioDao.findByEmail(usuario.getEmail()) == null) {

                Usuario usu = new Usuario();

                var role = roleDao.findById((long) 2).get();

                usu.setNombre(usuario.getNombre());
                usu.setApellidos(usuario.getApellidos());
                usu.setEmail(usuario.getEmail());
                usu.setPassword(passwordEncoder.encode(usuario.getPassword()));
                usu.setUsername(usuario.getUsername());
                usu.setEnabled(true);

                usu.setRole(role);

                return usuarioDao.save(usu);
            }

            FailRegister error = new FailRegister();
            error.setNameError("duplicated_email");
            error.setMessage("El email ingresado ya esta registrado.");

            return error;

        }

        FailRegister error = new FailRegister();
        error.setNameError("duplicated_username");
        error.setMessage("El username ingresado ya esta registrado.");

        return error;
    }

    @Override
    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioDao.findByUsername(username);
    }

}
