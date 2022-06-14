package com.leancoder.photogallery.provider.cloudinary;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.leancoder.photogallery.provider.cloudinary.interfaces.ICloudinaryProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryProvider implements ICloudinaryProvider {

    // Carga de bean que inyecta el servicio cloudinary ya inicializado para operarlo.
    @Autowired
    @Qualifier("cloudinaryInit")
    private Cloudinary cloudinaryProvider;

    // Metodo que extrae la extension de un archivo
    public static String fileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    // Metodo para la subida de un archivo al servidor de cloudinary
    @Override
    public Map<String, Object> upload(MultipartFile file) {

        Map<String, Object> res = new HashMap<String, Object>();

        if (CloudinaryProvider.fileExtension(file.getOriginalFilename()).equals("png") || CloudinaryProvider.fileExtension(file.getOriginalFilename()).equals("jpeg") || CloudinaryProvider.fileExtension(file.getOriginalFilename()).equals("jpg")) {
            try {
                // Se retorna un map como resultado de la consulta:
                var uploadPhotoResult = cloudinaryProvider.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                res.put("url", uploadPhotoResult.get("url").toString());
                res.put("id", uploadPhotoResult.get("public_id").toString());
                res.put("message", "Success upload");
                res.put("isUpload", true);
                return res;
            } catch (Exception e) {
                res.put("message", "Unknown error");
                res.put("isUpload", false);
                return res; 
            }
        } else {
            res.put("message", "Invalid file");
            res.put("isUpload", false);
            return res;
        }

    }

    // Metodo para eliminar una foto del servidor cloudinary
    @Override
    public Map<String, Object> delete(String public_id) throws IOException {

        Map<String, Object> res = new HashMap<String, Object>();
        var deleteResponse = cloudinaryProvider.uploader().destroy(public_id, ObjectUtils.asMap("resource_type","image"));
        var isDelete = deleteResponse.get("result").equals("ok") ? true:false;
        res.put("isDelete", isDelete);
        
        return res;
    }

}
