package com.leancoder.photogallery.models.entities.user;

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
import javax.validation.constraints.Size;

import com.leancoder.photogallery.models.entities.photo.FavoritePhoto;
import com.leancoder.photogallery.models.entities.photo.LikesPhoto;
import com.leancoder.photogallery.models.entities.photo.Photo;

import org.springframework.format.annotation.DateTimeFormat;

/* 
    * Entidad para el registro de un usuario en la BD.
    * Maneja la relacion con su rol "de muchos(usuarios) apuntando a uno(un solo rol, existen diferentes roles) "
    * Maneja la relacion con sus fotos "de uno(1 usuario) apuntando a muchos(varias fotos) "
 */
@Entity
@Table(name="users")
public class User {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Size(max=60)
    private String nombre;

    @Size(max=60)
    private String apellidos;

    @Size(max=70)
    @Column(unique=true)
    private String email;

    @Size(max=60)
    private String password;

    @Size(max=60)
    @Column(unique=true)
    private String username;

    private Boolean enabled;

    @Size(max = 200)
    private String description;

    @Column(name = "fecha_registro")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fechaRegistro;

    @ManyToOne(targetEntity = RoleUser.class)
    @JoinColumn(name = "auth_id", nullable = true, referencedColumnName="id")
    private RoleUser role;

    @ManyToOne(targetEntity = GenderUser.class)
    @JoinColumn(name = "gender_id", nullable = false)
    private GenderUser gender;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Photo> photos;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<LikesPhoto> likesPhoto;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<FavoritePhoto> favorites;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public List<FavoritePhoto> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<FavoritePhoto> favorites) {
        this.favorites = favorites;
    }

    public List<LikesPhoto> getLikesPhoto() {
        return likesPhoto;
    }

    public void setLikesPhoto(List<LikesPhoto> likesPhoto) {
        this.likesPhoto = likesPhoto;
    }

    public GenderUser getGender() {
        return gender;
    }

    public void setGender(GenderUser gender) {
        this.gender = gender;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public RoleUser getRole() {
        return role;
    }

    public void setRole(RoleUser role) {
        this.role = role;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

}
