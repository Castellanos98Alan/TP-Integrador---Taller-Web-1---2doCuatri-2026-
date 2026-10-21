package com.tallerwebi.dominio.excepcion;

public class SolicitudAmistadInvalida extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SolicitudAmistadInvalida(String mensaje) {
    super(mensaje);
  }
}
