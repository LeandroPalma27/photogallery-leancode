package com.leancoder.photogallery.models.domains.validators;

/* 
 * DTO para validar los endopoints de tipo REST.
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
