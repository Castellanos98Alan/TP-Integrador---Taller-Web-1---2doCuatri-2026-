package com.tallerwebi.dominio.excepcion;

public class CanchaNoEncontradaException extends ExcepcionNegocio {

  private static final long serialVersionUID = 1L;

  public CanchaNoEncontradaException(String mensaje) {
    super(mensaje);
  }
}
