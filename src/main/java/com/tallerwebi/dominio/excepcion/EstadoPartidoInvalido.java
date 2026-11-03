package com.tallerwebi.dominio.excepcion;

public class EstadoPartidoInvalido extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public EstadoPartidoInvalido(String message) {
    super(message);
  }
}
