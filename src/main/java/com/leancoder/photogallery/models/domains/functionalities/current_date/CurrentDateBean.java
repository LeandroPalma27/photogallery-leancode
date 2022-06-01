package com.leancoder.photogallery.models.domains.functionalities.current_date;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CurrentDateBean{

    private DateTimeFormatter formatter;

    private String timePattern;

    public CurrentDateBean(String formatter) {
        this.formatter = DateTimeFormatter.ofPattern(formatter);
        this.timePattern = formatter;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public String getTimePattern() {
        return timePattern;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    // Metodo para obtener la fecha(se requiere crear un objeto con parametros como el formato de la fecha).
    public Date getCurrentDate() {

        var fechaActualString = this.formatter.format(LocalDateTime.now());
        return new SimpleDateFormat(this.timePattern).parse(fechaActualString, new ParsePosition(0));

    }

}