package com.leancoder.photogallery.view.pdf;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.leancoder.photogallery.models.entities.photo.Photo;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

// Un endpoint carga una ruta de alguna vista, dentro del classpath de vistas. Al cargarse, este component anotado con la misma direccion de ruta, se ejecutara.
@Component("photos/details")
// En lugar de cargarse la vista, se carga este builder de PDF, mostrando el render del pdf en cuestion.
public class PhotoPdfView extends AbstractPdfView{

    // Inyectamos la fuenta de nuestros textos en diferentes idiomas:
    @Autowired
    private MessageSource messageSource;

    // Inyectamos localeResolver para implementar la configuracion regional por default:
    @Autowired
    private LocaleResolver localeResolver;

    /** 
    * Se implementa de manera obligatoria, porque esta anotada con
    * "abstract", y al ser asi, es como si usaramos una interface, e implementaramos
    * los metodos que esta tiene (SOLO EN CASO DE HEREDAR CLASES).
    **/
    @Override
    // Se carga "application/pdf":
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        Photo foto = (Photo) model.get("photoDetails");

        // Obtenemos la configuracion local desde el request(POR DEFECTO SIEMPRE ES "es", y en cualquier momento de la ejecucion de nuestra app cargara una region):
        // Se puede ver afectado por un cambio desde el cliente:
        Locale locale = localeResolver.resolveLocale(request);
        
        PdfPTable tabla1 = new PdfPTable(1);

        Font font = new Font(1);
        font.setColor(9, 201, 121);
        Chunk chunk1 = new Chunk("Photogallery", font);
        Paragraph frase = new Paragraph(chunk1);
        frase.setAlignment(Element.ALIGN_CENTER);
        PdfPCell cell = new PdfPCell();
        cell.addElement(frase);
        cell.setMinimumHeight(30);
        tabla1.addCell(cell);


        PdfPTable tabla2 = new PdfPTable(1);
        tabla2.setSpacingBefore(10);

        PdfPCell filaAutor = new PdfPCell();
        filaAutor.setColspan(2);
        Font fontForAuthorTitle = new Font();
        fontForAuthorTitle.setColor(0, 84, 117);
        // Indicamos que el texto a cargar sera el siguiente, mas un null y mostrando en que region esta la aplicaciom, para que el messageSource cargue el idioma correcto: 
        filaAutor.addElement(new Phrase(messageSource.getMessage("text.pdf.autor.title", null, locale), font));
        filaAutor.addElement(new Phrase(foto.getUser().getNombre().concat(" ").concat(foto.getUser().getApellidos())));

        tabla2.addCell(filaAutor);

        PdfPCell filaPhoto = new PdfPCell();
        filaPhoto.setRowspan(1);
        filaPhoto.setColspan(2);
        filaPhoto.setPhrase(new Phrase("Foto: ", fontForAuthorTitle));
        filaPhoto.setPhrase(new Phrase(foto.getUrlPhoto()));
        
        tabla2.addCell(filaPhoto);

        PdfPCell filaDesc = new PdfPCell();
        filaDesc.setRowspan(1);
        filaDesc.setColspan(2);
        filaDesc.setPhrase(new Phrase("Descripcion:  ", fontForAuthorTitle));
        filaDesc.setPhrase(new Phrase(foto.getDescription()));
        
        tabla2.addCell(filaDesc);

        document.add(tabla1);
        document.add(tabla2);
    }


}
