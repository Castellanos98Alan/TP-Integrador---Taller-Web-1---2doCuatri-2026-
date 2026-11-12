package com.tallerwebi.dominio.excepcion;

public class CodigoAccesoInvalidoException extends ExcepcionNegocio {

  private static final long serialVersionUID = 1L;

  public CodigoAccesoInvalidoException(String mensaje) {
    super(mensaje);
  }
}
