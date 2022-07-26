package com.leancoder.photogallery.models.domains.validators;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

/* 
 * DTO para validar el formulario que registra una foto en el servicio cloudinary y base de datos.
 */
public class PhotoUploaderValidator {
    
    @NotBlank(message = "Coloca un titulo.")
    @Size(max = 60, message = "Not exceed 60 characters.")
    private String title;

    @NotBlank(message = "Coloca una descripcion.")
    @Size(max = 200, message = "Not exceed 200 characters.")
    private String description;

    private MultipartFile file;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

}
