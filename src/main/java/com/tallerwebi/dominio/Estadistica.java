package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
public class Estadistica {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer goles;
  private Integer asistencias;
  private Integer amarillas;
  private Integer rojas;
  private Integer puntaje;

  @ManyToOne(fetch = FetchType.EAGER)
  private Partido partido;

  @ManyToOne
  private Jugador jugador;

  public Estadistica() {}

  public Estadistica(
    Integer goles,
    Integer asistencias,
    Integer amarillas,
    Integer rojas,
    Integer puntaje,
    Jugador jugador,
    Partido partido
  ) {
    this.goles = goles;
    this.asistencias = asistencias;
    this.amarillas = amarillas;
    this.rojas = rojas;
    this.puntaje = puntaje;
    this.jugador = jugador;
    this.partido = partido;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getGoles() {
    return goles;
  }

  public void setGoles(Integer goles) {
    this.goles = goles;
  }

  public Integer getAsistencias() {
    return asistencias;
  }

  public void setAsistencias(Integer asistencias) {
    this.asistencias = asistencias;
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

  public Integer getPuntaje() {
    return puntaje;
  }

  public void setPuntaje(Integer puntaje) {
    this.puntaje = puntaje;
  }

  public Jugador getJugador() {
    return jugador;
  }

  public void setJugador(Jugador jugador) {
    this.jugador = jugador;
  }

  public Partido getPartido() {
    return partido;
  }

  public void setPartido(Partido partido) {
    this.partido = partido;
  }
}
