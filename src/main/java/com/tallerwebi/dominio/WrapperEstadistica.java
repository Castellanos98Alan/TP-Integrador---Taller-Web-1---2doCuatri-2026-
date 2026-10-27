package com.tallerwebi.dominio;

import java.util.List;

public class WrapperEstadistica {

  private List<DTOEstadisticaJugador> estadisticas;

  public List<DTOEstadisticaJugador> getEstadisticas() {
    return estadisticas;
  }

  public void setEstadisticas(List<DTOEstadisticaJugador> estadisticas) {
    this.estadisticas = estadisticas;
  }
}
