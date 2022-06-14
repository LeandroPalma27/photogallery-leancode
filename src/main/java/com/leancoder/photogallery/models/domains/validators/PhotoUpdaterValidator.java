package com.leancoder.photogallery.models.domains.validators;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/* 
 * DTO para validar el formulario que actualiza los detalles de una foto(titulo y descripcion)
 */
public class PhotoUpdaterValidator {
    
    @NotBlank(message = "Place the title.")
    @Size(max = 60, message = "Not exceed 60 characters.")
    private String title;

    @NotBlank(message = "Place the description.")
    @Size(max = 200, message = "Not exceed 200 characters.")
    private String description;

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

}
