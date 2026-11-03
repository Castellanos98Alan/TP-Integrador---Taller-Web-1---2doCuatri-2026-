package com.tallerwebi.dominio.excepcion;

public class CupoInvalidoException extends ExcepcionNegocio {

  private static final long serialVersionUID = 1L;

  public CupoInvalidoException(String mensaje) {
    super(mensaje);
  }
}
