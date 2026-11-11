package com.tallerwebi.dominio.servicio;

@FunctionalInterface
public interface ServicioEmail {
  void enviarCorreo(String destinatario, String asunto, String mensaje);
}
