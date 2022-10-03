package com.leancoder.photogallery.view.json;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Component("photos/details.json")
public class PhotoDetailsJsonView extends MappingJackson2JsonView{

    @Override
    protected Object filterModel(Map<String, Object> model) {
        model.remove("title");
        model.remove("photoUpdater");
        model.remove("isLiked");
        model.remove("isSaved");
        model.remove("user");
        model.remove("profilePictureUser");
        model.remove("usuario");
        var isProfilePhoto = model.get("isProfilePhoto");
        if (isProfilePhoto != null) {
            model.remove("isProfilePhoto");
        }
        return super.filterModel(model);
    }

}