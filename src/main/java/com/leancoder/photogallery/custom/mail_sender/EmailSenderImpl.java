package com.leancoder.photogallery.custom.mail_sender;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.MimeMessage;
import javax.swing.text.AbstractDocument.Content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Component
public class EmailSenderImpl implements IEmailService {

    private static final String EMAIL_SIMPLE_TEMPLATE_NAME = "html/prueba";

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendSimpleMessage(String to, String subject, String text) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("photogallerysupp@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
        
    }

    @Override
    public void sendMessageUsingThymeleafTemplate(String to, String subject, String templateType, Map<String, Object> templateModel) throws MessagingException {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = templateEngine.process(EMAIL_SIMPLE_TEMPLATE_NAME.concat(".html"), thymeleafContext);
        sendHtmlMessage(to, subject, htmlBody);
    }


}
