package com.leancoder.photogallery.models.domains.validators;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

// Clase para validar formulario de cambio de email
public class EnterEmailValidator {
    
    @Email
    @NotBlank
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
