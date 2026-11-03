package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class WrapperEstadistica {

  private List<DTOEstadisticaJugador> estadisticas = new ArrayList<>();

  public List<DTOEstadisticaJugador> getEstadisticas() {
    return estadisticas;
  }

  public void setEstadisticas(List<DTOEstadisticaJugador> estadisticas) {
    this.estadisticas = estadisticas;
  }
}
