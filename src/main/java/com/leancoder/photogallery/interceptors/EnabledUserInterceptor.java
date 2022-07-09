package com.leancoder.photogallery.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

@Component("enabledUserInterceptor")
public class EnabledUserInterceptor implements HandlerInterceptor {

    @Autowired
    IUsuarioService usuarioService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        SecurityContext sc = SecurityContextHolder.getContext();
        Authentication authentication = sc.getAuthentication();
        var realUsername = authentication.getName().equals("anonymousUser") ? false : true;
        if (realUsername) {
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
            var isVerified = usuario.getEnabled() == true ? true : false;
            if (isVerified) {
                return true;
            }
            FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);
            outputFlashMap.put("errorMessage",
                    "Su cuenta no esta verificada, revise su correo y en caso de que la solicitud haya expirado genere otra desde su perfil.");
            RequestContextUtils.saveOutputFlashMap(request.getContextPath() + "/account", request, response);
            response.sendRedirect(request.getContextPath() + "/account");
            return false;
        }
        FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);
        outputFlashMap.put("errorMessage",
                "Verifique su cuenta para asi poder acceder a las demas herramientas de Photogallery.");
        RequestContextUtils.saveOutputFlashMap(request.getContextPath() + "/account", request, response);
        response.sendRedirect(request.getContextPath() + "/account");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        if (request.getMethod().equalsIgnoreCase("post")) {
            return;
        }
    }

}
