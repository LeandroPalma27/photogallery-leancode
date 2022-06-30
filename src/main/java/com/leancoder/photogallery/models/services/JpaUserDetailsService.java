package com.leancoder.photogallery.models.services;

import java.util.ArrayList;
import java.util.List;

import com.leancoder.photogallery.models.dao.IUsuarioDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService{

    @Autowired
    IUsuarioDao usuarioDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var usuario = usuarioDao.findByUsername(username);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(usuario.getRole().getAuthority()));
        
        if (username == null || username == "" || authorities.isEmpty() || usuario.getEnabled() == false) {
            throw new UsernameNotFoundException("Error al loguear: Credenciales invalidas, usuario sin rol asignado o cuenta no verificada.");
        }

        return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true, authorities);

    }
    
}
