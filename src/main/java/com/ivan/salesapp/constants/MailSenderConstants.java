package com.ivan.salesapp.constants;

public interface MailSenderConstants {
    String HOST = "smtp.gmail.com";
    int PORT = 587;

    String USERNAME = "design.your.home.store@gmail.com";
    String PASSWORD = "designyourhomenow";

    String SMTP = "mail.transport.protocol";
    String AUTH = "mail.smtp.auth";
    String TLS = "mail.smtp.starttls.enable";
    String DEBUG = "mail.debug";

    String MESSAGE_SUBJECT = "WARNING:%S quantity is critical!";
    String MESSAGE_TEXT = "Dear Administrator,\n\nThis email was sent to inform you " +
            "that the quantity of %s with product-ID:%s\n" +
            "is low and below the minimum of 10 in stock.\n We recommend you to restock.\n" + "--Details: \n" +
            "---Last order proceeded: \n---Customer: \n" + "---Ordered: %s\n" + "---Remaining: %s\n\n" +
            "Please do not reply to this email! It was sent by the system of Home Design Store.\n" +
            "Thank you!\n\n" +
            "Best Regards,\n" +
            "Team Home Design Store";
}
