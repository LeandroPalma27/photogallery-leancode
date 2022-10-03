package com.leancoder.photogallery.models.domains.responses;

// Clase dto para recibir datos de una consulta sql que muestra solo un campo de una tabla (tabla de likes_photo)
public class FotosConMasLikes {

    // Solo se almacena el db_id de la foto:
    private Long photo_id;

    public Long getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(Long photo_id) {
        this.photo_id = photo_id;
    }
    
}
