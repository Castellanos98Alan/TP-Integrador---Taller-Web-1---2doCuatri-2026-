package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Resultado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer golesPrimerEquipo;
  private Integer golesSegundoEquipo;
  private Equipo ganador;
  private Equipo perdedor;
  private LocalDateTime fechaRegistro;

  public Resultado(
    Integer id,
    Integer golesPrimerEquipo,
    Integer golesSegundoEquipo,
    Equipo ganador,
    Equipo perdedor,
    LocalDateTime fechaRegistro
  ) {
    this.id = id;
    this.golesPrimerEquipo = golesPrimerEquipo;
    this.golesSegundoEquipo = golesSegundoEquipo;
    this.ganador = ganador;
    this.perdedor = perdedor;
    this.fechaRegistro = fechaRegistro;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getGolesPrimerEquipo() {
    return golesPrimerEquipo;
  }

  public void setGolesPrimerEquipo(Integer golesPrimerEquipo) {
    this.golesPrimerEquipo = golesPrimerEquipo;
  }

  public Integer getGolesSegundoEquipo() {
    return golesSegundoEquipo;
  }

  public void setGolesSegundoEquipo(Integer golesSegundoEquipo) {
    this.golesSegundoEquipo = golesSegundoEquipo;
  }

  public Equipo getGanador() {
    return ganador;
  }

  public void setGanador(Equipo ganador) {
    this.ganador = ganador;
  }

  public Equipo getPerdedor() {
    return perdedor;
  }

  public void setPerdedor(Equipo perdedor) {
    this.perdedor = perdedor;
  }

  public LocalDateTime getFechaRegistro() {
    return fechaRegistro;
  }

  public void setFechaRegistro(LocalDateTime fechaRegistro) {
    this.fechaRegistro = fechaRegistro;
  }
}
