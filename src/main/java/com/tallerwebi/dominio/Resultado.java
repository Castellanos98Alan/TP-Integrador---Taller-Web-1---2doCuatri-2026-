package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Resultado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer golesLocal;
  private Integer golesVisitante;

  private LocalDateTime fechaRegistro;

  public Resultado() {}

  public Resultado(Integer golesLocal, Integer golesVisitante) {
    this.golesLocal = golesLocal;
    this.golesVisitante = golesVisitante;
    this.fechaRegistro = LocalDateTime.now();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getGolesLocal() {
    return golesLocal;
  }

  public void setGolesLocal(Integer golesPrimerEquipo) {
    this.golesLocal = golesPrimerEquipo;
  }

  public Integer getGolesVisitante() {
    return golesVisitante;
  }

  public void setGolesVisitante(Integer golesSegundoEquipo) {
    this.golesVisitante = golesSegundoEquipo;
  }

  public LocalDateTime getFechaRegistro() {
    return fechaRegistro;
  }

  public void setFechaRegistro(LocalDateTime fechaRegistro) {
    this.fechaRegistro = fechaRegistro;
  }
}
