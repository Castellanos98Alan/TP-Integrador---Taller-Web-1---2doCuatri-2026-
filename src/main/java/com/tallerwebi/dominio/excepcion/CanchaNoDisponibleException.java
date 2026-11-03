package com.tallerwebi.dominio.excepcion;

public class CanchaNoDisponibleException extends ExcepcionNegocio {

  private static final long serialVersionUID = 1L;

  public CanchaNoDisponibleException(String mensaje) {
    super(mensaje);
  }
}
