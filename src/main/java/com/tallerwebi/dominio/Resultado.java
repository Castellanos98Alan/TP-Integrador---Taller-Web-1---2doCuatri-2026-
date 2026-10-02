package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultado")
public class Resultado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer golesPrimerEquipo;
  private Integer golesSegundoEquipo;

  // Hibernate crea por defecto las columnas ganador_id y perdedor_id
  @ManyToOne(fetch = FetchType.EAGER)
  private Equipo ganador;

  @ManyToOne(fetch = FetchType.EAGER)
  private Equipo perdedor;

  private LocalDateTime fechaRegistro;

  // 1. Constructor vacío OBLIGATORIO para Hibernate
  public Resultado() {}

  // 2. Constructor con parámetros para tu lógica
  public Resultado(
    Integer golesPrimerEquipo,
    Integer golesSegundoEquipo,
    Equipo ganador,
    Equipo perdedor,
    LocalDateTime fechaRegistro
  ) {
    this.golesPrimerEquipo = golesPrimerEquipo;
    this.golesSegundoEquipo = golesSegundoEquipo;
    this.ganador = ganador;
    this.perdedor = perdedor;
    this.fechaRegistro = fechaRegistro;
  }

  // Getters y Setters...
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
