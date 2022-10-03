package com.leancoder.photogallery.view.xml.photos;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.xml.MarshallingView;

import com.leancoder.photogallery.models.entities.photo.Photo;

// Vista que carga los datos en XML(?format=xml)
@Component("photos/all.xml")
public class PhotosListXmlView extends MarshallingView {

    // Pasamos por el constructor la objeto que serializa (DE JAVA OBJECT A XML)
    @Autowired
    public PhotosListXmlView(Jaxb2Marshaller marshaller) {
        super(marshaller);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // Al heredar el metodo, tenemos que pasar el model, asi que debemos quitar del model todo objeto que no nos interesa
        // (NO ESTAN MAPEADOS EN LA SERIALIZACION, Y LA LISTA DE FOTOS TAMPOCO, YA QUE QUIEREN SER MAPEADAS A LA ENVOLTURA).
        model.remove("title");
        model.remove("page");
        @SuppressWarnings("unchecked")
        Page<Photo> photos = (Page<Photo>) model.get("photos");
        // Si se quieren todos los registros, se hace una consulta desde aca, que traiga todos los registros.
        PhotosList listaFotos = new PhotosList(photos.getContent());
        model.put("photosList", listaFotos);
        super.renderMergedOutputModel(model, request, response);
    }
    


}
