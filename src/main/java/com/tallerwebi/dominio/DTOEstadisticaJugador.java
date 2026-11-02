package com.tallerwebi.dominio;

public class DTOEstadisticaJugador {

  private Long idJugador;
  private Integer goles;
  private Integer amarillas;
  private Integer rojas;
  private Integer asistencias;

  public DTOEstadisticaJugador() {}

  public DTOEstadisticaJugador(
    Long idJugador,
    Integer goles,
    Integer amarillas,
    Integer rojas,
    Integer asistencias
  ) {
    this.idJugador = idJugador;
    this.goles = goles;
    this.amarillas = amarillas;
    this.rojas = rojas;
    this.asistencias = asistencias;
  }

  public Long getIdJugador() {
    return idJugador;
  }

  public void setIdJugador(Long idJugador) {
    this.idJugador = idJugador;
  }

  public Integer getGoles() {
    return goles;
  }

  public void setGoles(Integer goles) {
    this.goles = goles;
  }

  public Integer getAmarillas() {
    return amarillas;
  }

  public void setAmarillas(Integer amarillas) {
    this.amarillas = amarillas;
  }

  public Integer getRojas() {
    return rojas;
  }

  public void setRojas(Integer rojas) {
    this.rojas = rojas;
  }

  public Integer getAsistencias() {
    return asistencias;
  }

  public void setAsistencias(Integer asistencias) {
    this.asistencias = asistencias;
  }
}
