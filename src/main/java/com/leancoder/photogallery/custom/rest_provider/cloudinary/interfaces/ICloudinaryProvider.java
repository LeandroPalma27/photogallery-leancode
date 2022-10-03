package com.leancoder.photogallery.custom.rest_provider.cloudinary.interfaces;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

// Interfaz que implementa funcionalidades para el uso del provedor de service de cloudinary:
public interface ICloudinaryProvider {
    
    // Funcion para la subida de archivos:
    public Map<String, Object> upload(MultipartFile file);

    // Funcion para eliminar un archivo:
    public Map<String, Object> delete(String public_id) throws IOException;

}
