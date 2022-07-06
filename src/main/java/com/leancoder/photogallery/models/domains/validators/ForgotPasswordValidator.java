package com.leancoder.photogallery.models.domains.validators;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.leancoder.photogallery.custom.annotations.password.ValidPassword;

public class ForgotPasswordValidator {
    
    @NotBlank(message = "Rellene el campo.")
    @ValidPassword
    private String newPassword;

    @NotBlank(message = "Rellene el campo.")
    private String confirmPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
