package com.leancoder.photogallery.models.domains.responses;

/* 
 * DTO para el manejo de respuestas luego procesar endopoints de tipo REST.
    Se usa para dar like, quitar like o guardar y eliminar fotos de la lista de guardados.
    "isSuccesful" es un atributo para saber si se proceso dicha cosa con satisfaccion(true o false).
    "Message" es para describir en que consiste el error.
 */
public class RestRequestResponse {
    
    private Boolean isSuccesful;
    private String message;
    private String description;

    public RestRequestResponse() {
    }

    public Boolean getIsSuccesful() {
        return isSuccesful;
    }

    public void setIsSuccesful(Boolean isSuccesful) {
        this.isSuccesful = isSuccesful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
