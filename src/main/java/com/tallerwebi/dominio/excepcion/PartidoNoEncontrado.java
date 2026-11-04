package com.tallerwebi.dominio.excepcion;

public class PartidoNoEncontrado extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public PartidoNoEncontrado(String message) {
    super(message);
  }
}
