package com.leancoder.photogallery.models.entities.photo;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.leancoder.photogallery.models.entities.user.User;

import org.springframework.format.annotation.DateTimeFormat;

/* 
    * Entidad para el registro de las fotos de un usuario
    * Se creo con la finalidad de tener un diseño de uno(1 usuario) a muchos(muchas fotos).
 */
@Entity
@Table(name = "photos")
public class Photo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="db_id")
    private Long id;

    @Column(name = "url_photo")
    private String urlPhoto;

    @Column(unique = true, name = "upload_id")
    private String uploadId;

    private String description;

    private String title;

    private Long likes;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName="id", updatable = false)
    private User usuario;

    @OneToMany(mappedBy = "photo", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RolePhoto> roles;

    @OneToMany(mappedBy = "photo", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<LikesPhoto> likesPhoto;

    @Column(name = "fecha_registro")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<LikesPhoto> getLikesPhoto() {
        return likesPhoto;
    }

    public void setLikesPhoto(List<LikesPhoto> likesPhoto) {
        this.likesPhoto = likesPhoto;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public List<RolePhoto> getRoles() {
        return roles;
    }

    public void setRoles(List<RolePhoto> roles) {
        this.roles = roles;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
