package com.tallerwebi.dominio;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.servicio.ServicioEmail;
import com.tallerwebi.dominio.servicio.ServicioEmailImpl;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class ServicioEmailTest {

  @Test
  public void queSeEnvieUnCorreoAlJugador() {
    // Preparacion
    JavaMailSender mailSenderMock = mock(JavaMailSender.class);

    ServicioEmail servicioEmail = new ServicioEmailImpl(mailSenderMock);

    String destinatario = "jugador@ejemplo.com";
    String asunto = "Recordatorio de partido";
    String mensaje = "Tu partido comienza a las 20:00";

    // Ejecucion
    servicioEmail.enviarCorreo(destinatario, asunto, mensaje);

    // Verificacion
    verify(mailSenderMock, times(1))
      .send(
        argThat((SimpleMailMessage correo) ->
          destinatario.equals(correo.getTo()[0]) &&
          asunto.equals(correo.getSubject()) &&
          mensaje.equals(correo.getText())
        )
      );
  }
}
