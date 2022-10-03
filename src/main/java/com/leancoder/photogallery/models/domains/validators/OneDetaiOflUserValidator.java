package com.leancoder.photogallery.models.domains.validators;

import javax.validation.constraints.NotBlank;

public class OneDetaiOflUserValidator {

    /* Esta clase DTO es para cuando queramos validar algun formulario de actualizacion de datos de
    un usuario, pero que solo actualice un atributo en la entidad. */

    @NotBlank(message="Rellene el campo.")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
