package com.tallerwebi.dominio.excepcion;

public class OrganizadorNoPuedeAbandonarException extends ExcepcionNegocio {

  private static final long serialVersionUID = 1L;

  public OrganizadorNoPuedeAbandonarException(String mensaje) {
    super(mensaje);
  }
}
