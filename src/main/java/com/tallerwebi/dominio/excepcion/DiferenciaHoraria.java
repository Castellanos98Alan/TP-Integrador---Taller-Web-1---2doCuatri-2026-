package com.tallerwebi.dominio.excepcion;

public class DiferenciaHoraria extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DiferenciaHoraria(String message) {
    super(message);
  }
}
