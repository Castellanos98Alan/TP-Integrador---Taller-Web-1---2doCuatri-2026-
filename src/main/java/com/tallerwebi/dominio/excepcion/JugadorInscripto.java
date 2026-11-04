package com.tallerwebi.dominio.excepcion;

public class JugadorInscripto extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public JugadorInscripto(String message) {
    super(message);
  }
}
