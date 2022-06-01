package com.leancoder.photogallery.provider.cloudinary.interfaces;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryProvider {
    
    public Map<String, Object> upload(MultipartFile file);

    public Map<String, Object> delete(String public_id) throws IOException;

}
