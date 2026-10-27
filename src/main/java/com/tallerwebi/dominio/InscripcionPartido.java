package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class InscripcionPartido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partido_id")
  private Partido partido;

  @ManyToOne
  @JoinColumn(name = "jugador_id")
  private Jugador jugador;

  @ManyToOne
  @JoinColumn(name = "equipo_id")
  private Equipo equipo;

  private Boolean confirmado = true;

  private boolean esCapitan;

  private LocalDateTime fechaInscripcion;

  public InscripcionPartido() {}

  public InscripcionPartido(Partido partido, Jugador jugador) {
    this.partido = partido;
    this.jugador = jugador;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Partido getPartido() {
    return partido;
  }

  public void setPartido(Partido partido) {
    this.partido = partido;
  }

  public Jugador getJugador() {
    return this.jugador;
  }

  public void setJugador(Jugador jugador) {
    this.jugador = jugador;
  }

  public Equipo getEquipo() {
    return equipo;
  }

  public void setEquipo(Equipo equipo) {
    this.equipo = equipo;
  }

  public Boolean getConfirmado() {
    return confirmado;
  }

  public void setConfirmado(Boolean confirmado) {
    this.confirmado = confirmado;
  }

  public boolean isEsCapitan() {
    return esCapitan;
  }

  public void setEsCapitan(boolean esCapitan) {
    this.esCapitan = esCapitan;
  }

  public LocalDateTime getFechaInscripcion() {
    return fechaInscripcion;
  }

  public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
    this.fechaInscripcion = fechaInscripcion;
  }
}
