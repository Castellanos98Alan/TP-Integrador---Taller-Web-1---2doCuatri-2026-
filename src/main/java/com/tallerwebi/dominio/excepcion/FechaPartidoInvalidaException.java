package com.tallerwebi.dominio.excepcion;

public class FechaPartidoInvalidaException extends ExcepcionNegocio {

  private static final long serialVersionUID = 1L;

  public FechaPartidoInvalidaException(String mensaje) {
    super(mensaje);
  }
}
