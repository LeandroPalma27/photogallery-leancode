package com.leancoder.photogallery.view.xlsx;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.leancoder.photogallery.models.entities.photo.Photo;

// Se anota el componente con otro nombre, ya que no pueden existir componentes con el nombre repetido.
// Y como ya se sabe, la aplicacion detecta a traves del parametro "format", si alguna data (obtenida desde un path endpoint en nuestro app) requiere ser mostrada en algun otro formato diferente a HTML,
// si se detecta que la vista es solicitada con un "?format=xlsx (O PARECIDO), se carga este componente y se renderiza la vista en formato EXCEL."
@Component("photos/details.xlsx")
public class PhotoXlsxView extends AbstractXlsxView {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        response.setHeader("Content-Disposition", "attachment; filename=\"photo_details.xlsx\"");
        Photo foto = (Photo) model.get("photoDetails");

        Locale locale = localeResolver.resolveLocale(request);

        Sheet sheet = workbook.createSheet("Detalles de foto");
        // Para cambiar el ancho de una celda, tenemos que cambiar el ancho de toda la columna:
        sheet.setColumnWidth(0, 25*256);

        // Crea una fila
        Row row1 = sheet.createRow(0);
        // Crea una columna dentro de esa fila(MAS UNA CELDA)
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue("Photogallery");
            

        Row row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue(messageSource.getMessage("text.xlsx.autor.title", null, locale));
        row2.createCell(1).setCellValue(foto.getUser().getNombre() + " ".concat(foto.getUser().getApellidos()));
        CellStyle estiloTitulo2 = workbook.createCellStyle();
        estiloTitulo2.setAlignment(HorizontalAlignment.CENTER);
        row2.getCell(0).setCellStyle(estiloTitulo2);
        Row row3 = sheet.createRow(2);
        row3.createCell(0).setCellValue("Titulo:");
        row3.createCell(1).setCellValue(foto.getTitle());
        row3.getCell(0).setCellStyle(estiloTitulo2);
        Row row4 = sheet.createRow(3);
        row4.createCell(0).setCellValue("Descripcion:");
        row4.createCell(1).setCellValue(foto.getDescription());
        row4.getCell(0).setCellStyle(estiloTitulo2);

        // Asi se da estilo a cada celda:
        CellStyle estiloTitulo = workbook.createCellStyle();
        estiloTitulo.setFillForegroundColor(IndexedColors.CORAL.index);
        estiloTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloTitulo.setAlignment(HorizontalAlignment.CENTER);

        // Se aplica a cada columna en especifico
        cell1.setCellStyle(estiloTitulo);


    }
    


}
