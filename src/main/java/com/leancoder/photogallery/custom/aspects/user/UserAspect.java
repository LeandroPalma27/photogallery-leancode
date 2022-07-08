package com.leancoder.photogallery.custom.aspects.user;

import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.view.RedirectView;

import com.leancoder.photogallery.models.dao.IUsuarioDao;

@Aspect
@Component
public class UserAspect {

    // Data access:
    @Autowired
    IUsuarioDao usuarioDao;
   
    // Puntos de corte

    // Consejos

    @Before("@annotation(com.leancoder.photogallery.custom.annotations.enabled_user.UserIsEnabled)")
    public void verificarUsuarioVerificado(JoinPoint point) {
        SecurityContext sc = SecurityContextHolder.getContext();
        Authentication authentication = sc.getAuthentication();
        // Si esta logueado imprime el nombre del usuario, si no esta logueado imprime "anonymousUser"
        if (!authentication.getName().equals("anonymousUser")) {
            if (!usuarioDao.findByUsername(authentication.getName()).getEnabled()) {
            }
            System.out.println(usuarioDao.findByUsername(authentication.getName()).getEnabled());
        }
    }

}
