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
import org.springframework.web.servlet.support.RequestContextUtils;

import com.leancoder.photogallery.models.services.user.interfaces.IUsuarioService;

@Component("enabledUserInterceptor")
// INTERCEPTOR QUE VERIFICA QUE UN USUARIO ESTE VERIFICADO (para asegurarse que de no pueda acceder a ciertas funciones dentro de la aplicacion)
public class EnabledUserInterceptor implements HandlerInterceptor {

    @Autowired
    IUsuarioService usuarioService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // Obtenemos el contexto de la sesion, para asi obtener el username:        
        SecurityContext sc = SecurityContextHolder.getContext();
        Authentication authentication = sc.getAuthentication();
        // Verificamos que alguien este autenticado(SI CARGA "anonymousUser" ES PORQUE NO HAY NADIE LOGUEADO):
        var realUsername = authentication.getName().equals("anonymousUser") ? false : true;
        // Si un usuario esta autenticado(SI NO LO ESTA, SE RETORNA TRUE), se verificara que este verificado:
        if (realUsername) {
            var usuario = usuarioService.obtenerUsuarioPorUsername(authentication.getName());
            var isVerified = usuario.getEnabled() == true ? true : false;
            // Si esta verificado, se retorna true y se procede al postHandle(ESTE NO REALIZA NINGUN CAMBIO EN EL MODELO)
            if (isVerified) {
                return true;
            }
            // Y en caso de que no lo este, se carga un flash con un mensaje, para mostrarlo luego de redireccionar la ruta
            FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);
            outputFlashMap.put("errorMessage",
                    "Para acceder a esta funcion su cuenta debe estar verificada, revise su correo. En caso de que la solicitud haya expirado, genere otra desde su perfil.");
            RequestContextUtils.saveOutputFlashMap(request.getContextPath() + "/account", request, response);
            response.sendRedirect(request.getContextPath() + "/account");
            return false;
        }
        /* FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);
        outputFlashMap.put("errorMessage",
                "Verifique su cuenta para asi poder acceder a las demas herramientas de Photogallery.");
        RequestContextUtils.saveOutputFlashMap(request.getContextPath() + "/account", request, response);
        response.sendRedirect(request.getContextPath() + "/account"); */
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        if (request.getMethod().equalsIgnoreCase("post")) {
            return;
        }
    }

}
