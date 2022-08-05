package com.leancoder.photogallery.models.domains.validators;

/* 
 * DTO para enviar la data de las fotos a modificar (likes y favoritos)
 */
public class RestRequestValidator {
    
    private String photo_id;
    private String user;
    
    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
