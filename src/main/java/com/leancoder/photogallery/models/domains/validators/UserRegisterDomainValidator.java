package com.leancoder.photogallery.models.domains.validators;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.leancoder.photogallery.custom.annotations.password.ValidPassword;

/* 
 * DTO para validar el formulario que registra un usuario en el sistema.
 */
public class UserRegisterDomainValidator {

    @Size(max=60, message = "No exceda los 60 caracteres.")
    @NotBlank(message = "Rellene el campo.")
    private String nombre;

    @Size(max=60, message = "No exceda los 60 caracteres.")
    @NotBlank(message = "Rellene el campo.")
    private String apellidos;

    @NotBlank(message = "Rellene el campo.")
    @Email(message = "Coloque un email valido.")
    private String email;

    @NotBlank(message = "Rellene el campo.")
    @Size(max=60, message = "No exceda los 60 caracteres.")
    @ValidPassword()
    private String password;

    @NotBlank(message = "Rellene el campo.")
    @Size(max=60, message = "No exceda los 60 caracteres.")
    private String confirmPassword;

    @NotBlank(message = "Rellene el campo.")
    @Size(max=60, message = "No exceda los 60 caracteres.")
    private String username;

    @NotNull(message = "Seleccione alguna opcion.")
    private Long genderId;

    public String getNombre() {
        return nombre;
    }

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(Long genderId) {
        this.genderId = genderId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
}
