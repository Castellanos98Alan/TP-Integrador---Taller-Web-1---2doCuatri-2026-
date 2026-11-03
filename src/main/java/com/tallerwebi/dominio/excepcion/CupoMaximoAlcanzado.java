package com.tallerwebi.dominio.excepcion;

public class CupoMaximoAlcanzado extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CupoMaximoAlcanzado(String message) {
    super(message);
  }
}
