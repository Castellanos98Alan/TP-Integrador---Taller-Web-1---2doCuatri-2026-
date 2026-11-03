package com.tallerwebi.dominio.excepcion;

public class RevisionEstadisticasNoPermitida extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public RevisionEstadisticasNoPermitida(String message) {
    super(message);
  }
}
