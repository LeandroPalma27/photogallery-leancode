package com.leancoder.photogallery.view.xml.photos;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.leancoder.photogallery.models.entities.photo.Photo;

// Clase wrapper que sirve para serializar una lista de entidades a XML.
@XmlRootElement(name = "photos")
public class PhotosList {
    
    // Mapeamos la lista de elementos, para que se serialice elemento por elemento:
    @XmlElement(name = "photo")
    private List<Photo> photos;

    public PhotosList() {}

    public PhotosList(List<Photo> photos) {
        this.photos = photos;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

}
