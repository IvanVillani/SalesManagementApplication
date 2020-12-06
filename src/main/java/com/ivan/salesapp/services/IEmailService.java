package com.ivan.salesapp.services;

import javax.mail.MessagingException;

public interface IEmailService {
    void sendSimpleMessage(String to, String subject, String text);

    void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) throws MessagingException;
}
