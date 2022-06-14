package com.leancoder.photogallery.models.domains.validators;

import javax.validation.constraints.Size;

import com.leancoder.photogallery.custom.annotations.password.ValidPassword;

/* 
 * DTO para validar el formulario que actualiza la constraseña de un usuario
 */
public class PasswordUserValidator {
    
    private String oldpass;

    @ValidPassword
    private String newpass;

    public String getOldpass() {
        return oldpass;
    }

    public void setOldpass(String oldpass) {
        this.oldpass = oldpass;
    }

    public String getNewpass() {
        return newpass;
    }

    public void setNewpass(String newpass) {
        this.newpass = newpass;
    }

}
