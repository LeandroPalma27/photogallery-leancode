package com.leancoder.photogallery.view.csv;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.leancoder.photogallery.models.entities.photo.Photo;

@Component("photos/all.csv")
// Para CSV usamos una clase abstracta mas general, ya que no contamos con una clase especifica para CSV.
public class PhotoCsvView extends AbstractView{

    public PhotoCsvView() {
        setContentType("text/csv");
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // Indicamos a la respuesta que el nombre de nuestro archivo sera este:
        response.setHeader("Content-Disposition", "attachment; filename=\"fotos.csv\"");        
        // Y tambien indicamos el tipo de contendio que se esta enviando:
        response.setContentType(getContentType());

        @SuppressWarnings("unchecked")
        Page<Photo> fotos = (Page<Photo>) model.get("photos");

        // Escribimos el archivo csv en el header
        ICsvBeanWriter beanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        // Forma alternativa de inicializar un array (Sirve para cuando queremos inicializar mas no crear una instancia de array).
        String [] header = new String [] {"id", "urlPhoto", "uploadId", "description", "title", "likes", "user", "roles", "date"};

        // En la cabecera del archivo:
        beanWriter.writeHeader(header);

        // Y en el cuerpo, iteramos foto por foto:
        for (Photo foto : fotos) {
            beanWriter.write(foto, header);
        }

        // Cerramos el beanWriter:
        beanWriter.close();

        // Un CSV personalizado, que contenga informacion de otras tablas que esten relacionados, se debe de hacer un un CustomerBean especifico para ICsvWriter 
        // (INVESTIGAR EN "http://super-csv.github.io/super-csv/examples_writing.html", tambien SE PUEDE HACER CON LISTAS).

        // La solicitud de una lista de objetos en otro formato, carga todo el model pasado en el handler. Asi que si tambien pasamos el numero de pagina
        // (PARA REGISTROS PAGINADOS), se cargaran solo los registros de esa pagina
    }
    
}
