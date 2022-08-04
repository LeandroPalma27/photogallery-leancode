package com.leancoder.photogallery.custom.mail_sender;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailSenderImpl implements IEmailService {

    // Inyeccion de interfaz para el envio de email con JAVA: 
    @Autowired
    private JavaMailSender emailSender;

    // Inyeccion de interfaz para procesar una plantilla thymeleaf:
    @Autowired
    private TemplateEngine templateEngine;


    // Estos 3 metodos reciben en comun 3 parametros: Receptor, asunto y mensaje.

    // Envia un mensaje simple a un correo:
    @Override
    public void sendSimpleMessage(String to, String subject, String text) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("photogallerysupp@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    // Envia un HTML simple:
    @Override
    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }

    // Envia un html, pero procesado como plantilla thymeleaf:
    @Override
    public void sendMessageUsingThymeleafTemplate(String to, String subject, String templateName, Map<String, Object> templateModel) throws MessagingException {
        Context thymeleafContext = new Context();
        // Incluimos el model para la plantilla thymelaf:
        thymeleafContext.setVariables(templateModel);
        // Procesamos la plantilla y lo enviamos como un HTML comun:
        String htmlBody = templateEngine.process("mail/".concat(templateName).concat(".html"), thymeleafContext);
        sendHtmlMessage(to, subject, htmlBody);
    }


}
