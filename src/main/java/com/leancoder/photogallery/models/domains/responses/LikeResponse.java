package com.leancoder.photogallery.models.domains.responses;

public class LikeResponse {
    
    private Boolean isLiked;
    private String message;
    private String description;

    public LikeResponse() {
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
