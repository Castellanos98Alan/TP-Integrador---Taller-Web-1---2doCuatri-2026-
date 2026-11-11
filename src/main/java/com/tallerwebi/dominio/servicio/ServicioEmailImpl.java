package com.tallerwebi.dominio.servicio;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ServicioEmailImpl implements ServicioEmail {

  private final JavaMailSender mailSender;

  public ServicioEmailImpl(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public void enviarCorreo(String destinatario, String asunto, String mensaje) {
    SimpleMailMessage correo = new SimpleMailMessage();

    correo.setFrom(System.getenv("MAIL_USERNAME"));
    correo.setTo(destinatario);
    correo.setSubject(asunto);
    correo.setText(mensaje);

    mailSender.send(correo);
  }
}
