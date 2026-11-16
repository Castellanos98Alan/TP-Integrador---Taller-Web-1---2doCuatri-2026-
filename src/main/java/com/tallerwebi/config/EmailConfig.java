package com.tallerwebi.config;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {

  @Bean
  public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);

    mailSender.setUsername(System.getenv("MAIL_USERNAME"));

    mailSender.setPassword(System.getenv("MAIL_PASSWORD"));

    Properties propiedades = mailSender.getJavaMailProperties();

    propiedades.put("mail.smtp.auth", "true");
    propiedades.put("mail.smtp.starttls.enable", "true");

    propiedades.put("mail.smtp.connectiontimeout", "5000");
    propiedades.put("mail.smtp.timeout", "5000");
    propiedades.put("mail.smtp.writetimeout", "5000");

    return mailSender;
  }
}
