package com.leancoder.photogallery.custom.mail_sender;

import java.util.Map;

import javax.mail.MessagingException;

public interface IEmailService {
    
    public void sendSimpleMessage(String to, String subject, String text) throws MessagingException;

    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException;

    public void sendMessageUsingThymeleafTemplate(String to, String subject, String templateName, Map<String, Object> templateModel) throws MessagingException;

}
