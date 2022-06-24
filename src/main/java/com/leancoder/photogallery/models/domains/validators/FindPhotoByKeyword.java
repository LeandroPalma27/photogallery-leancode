package com.leancoder.photogallery.models.domains.validators;

import javax.validation.constraints.NotBlank;

public class FindPhotoByKeyword {
    
    @NotBlank(message = "Rellene el campo.")
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

}
