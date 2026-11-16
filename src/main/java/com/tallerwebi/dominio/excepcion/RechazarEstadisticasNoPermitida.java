package com.tallerwebi.dominio.excepcion;

public class RechazarEstadisticasNoPermitida extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public RechazarEstadisticasNoPermitida(String message) {
    super(message);
  }
}
