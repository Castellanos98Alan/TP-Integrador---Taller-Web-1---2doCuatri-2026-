package com.tallerwebi.dominio.excepcion;

public class EstadisticasNoGuardadas extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public EstadisticasNoGuardadas(String message) {
    super(message);
  }
}
