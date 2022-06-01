package com.leancoder.photogallery.models.domains.validators;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class NameAndLastNameOfUserValidator {

    /* Esta clase DTO es para cuando queramos validar campos del formulario que actualizara nombres y apellidos de
    un usuario */
    
    @NotBlank(message="Rellene el campo.")
    private String name;

    @NotBlank(message="Rellene el campo.")
    private String lastname;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    
}
