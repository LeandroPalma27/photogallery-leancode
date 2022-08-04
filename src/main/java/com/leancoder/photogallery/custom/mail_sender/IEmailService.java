package com.leancoder.photogallery.custom.mail_sender;

import java.util.Map;

import javax.mail.MessagingException;

// Interfaz de implementacion de funcionalidades para el envio de correos desde la aplicacion
public interface IEmailService {
    
    // Implementacion de un envio de mensaje simple:
    public void sendSimpleMessage(String to, String subject, String text) throws MessagingException;

    // Implementacion de un mensaje HTML:
    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException;

    // Implementacion de un template HTML usando thymeleaf:
    public void sendMessageUsingThymeleafTemplate(String to, String subject, String templateName, Map<String, Object> templateModel) throws MessagingException;

}
