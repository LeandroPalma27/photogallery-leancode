package com.leancoder.photogallery.models.domains.responses;

/* 
 * DTO para el manejo de respuestas luego procesar formularios y realizar transacciones en la base de datos
    Se usa para registrar y/o actualizar.
    "Name" es un atributo para el manejo del nombre de respuesta, por ejemplo: "successful_update" o "unknown_error".
    "Message" es para describir en que consiste el error.
 */
public class UpdateOrRegisterDetailsResponse {
    
    private String name;

    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
