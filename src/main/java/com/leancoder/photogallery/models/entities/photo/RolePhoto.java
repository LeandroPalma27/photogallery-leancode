package com.leancoder.photogallery.models.entities.photo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/* 
    * Entidad para el registro de los roles de cada photo.
    * Se creo con un diseño de uno a muchos.
 */
@Entity
@Table(name="photo_roles")
public class RolePhoto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String role;

    @ManyToOne(targetEntity = Photo.class)
    @JoinColumn(nullable = false, name = "photo_id", referencedColumnName = "db_id", updatable = false)
    private Photo photo;

    public RolePhoto() {}

    public RolePhoto(String role, Photo photo) {
        this.role = role;
        this.photo = photo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    private static final long serialVersionUID = 1L;

}
