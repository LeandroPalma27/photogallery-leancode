package com.leancoder.photogallery.models.domain;

import com.leancoder.photogallery.models.entity.Usuario;

public class FailRegister extends Usuario {
    
    private String nameError;

    private String message;

    public String getNameError() {
        return nameError;
    }

    public void setNameError(String nameError) {
        this.nameError = nameError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
