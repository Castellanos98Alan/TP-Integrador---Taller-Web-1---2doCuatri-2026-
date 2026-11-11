package com.tallerwebi.dominio;

import com.tallerwebi.config.EmailConfig;
import com.tallerwebi.dominio.servicio.ServicioEmail;
import com.tallerwebi.dominio.servicio.ServicioEmailImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

public class EnvioEmailRealTest {

  @Test
  @EnabledIfEnvironmentVariable(named = "MAIL_TEST_ENABLED", matches = "true")
  public void queSeEnvieUnEmailReal() {
    EmailConfig configuracion = new EmailConfig();

    ServicioEmail servicioEmail = new ServicioEmailImpl(configuracion.javaMailSender());

    String destinatario = System.getenv("MAIL_TEST_TO");

    if (destinatario == null || destinatario.isBlank()) {
      throw new IllegalStateException("Falta configurar MAIL_TEST_TO");
    }

    String asunto = "Prueba de email - ArmoLaCancha";

    String mensaje =
      "Hola!\n\n" +
      "Este es un correo de prueba.\n\n" +
      "Partido: 20:00 hs\n" +
      "Presentacion: 19:50 hs\n\n" +
      "ArmoLaCancha";

    servicioEmail.enviarCorreo(destinatario, asunto, mensaje);
  }
}
