package com.ivan.salesapp.config;

import com.ivan.salesapp.constants.MailSenderConstants;
import com.ivan.salesapp.domain.entities.Discount;
import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import com.ivan.salesapp.domain.models.view.DiscountViewModel;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Properties;


@Configuration
public class ApplicationBeanConfiguration implements MailSenderConstants {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Discount.class, DiscountServiceModel.class)
                .addMapping(
                        src -> src.getCreator().getUsername(),
                        (dest, val) -> dest.setCreator((String) val)
                );
        modelMapper.createTypeMap(Discount.class, DiscountViewModel.class)
                .addMapping(
                        src -> src.getCreator().getUsername(),
                        (dest, val) -> dest.setCreator((String) val)
                );
        return modelMapper;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(HOST);
        mailSender.setPort(PORT);

        mailSender.setUsername(USERNAME);
        mailSender.setPassword(PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put(SMTP, "smtp");
        props.put(AUTH, "true");
        props.put(TLS, "true");
        props.put(DEBUG, "true");

        return mailSender;
    }
}
