package com.tallerwebi.dominio.excepcion;

public class ConfirmarEstadisticasNoPermitida extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ConfirmarEstadisticasNoPermitida(String message) {
    super(message);
  }
}
