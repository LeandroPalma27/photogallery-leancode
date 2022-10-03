package com.leancoder.photogallery.custom.annotations.password;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

// Anotacion para la validacion de una contraseña con un minimo de 8 y maximo de 30 caracteres, una letra mayuscula, un numero y un caracter especial.
@Documented
// Logica de anotacion:
@Constraint(validatedBy = PasswordConstraintValidator.class)
// A que apunta (en este caso es a atributos de una clase)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    
    String message() default "Invalid Password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
