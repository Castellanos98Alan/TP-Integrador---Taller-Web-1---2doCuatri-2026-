package com.tallerwebi.presentacion;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class DatosCrearPartido {

  private String cancha;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaHora;

  private Integer cupo;
  private String nivel;

  public DatosCrearPartido() {}

  public DatosCrearPartido(String cancha, LocalDateTime fechaHora, Integer cupo, String nivel) {
    this.cancha = cancha;
    this.fechaHora = fechaHora;
    this.cupo = cupo;
    this.nivel = nivel;
  }

  public String getCancha() {
    return cancha;
  }

  public void setCancha(String cancha) {
    this.cancha = cancha;
  }

  public LocalDateTime getFechaHora() {
    return fechaHora;
  }

  public void setFechaHora(LocalDateTime fechaHora) {
    this.fechaHora = fechaHora;
  }

  public Integer getCupo() {
    return cupo;
  }

  public void setCupo(Integer cupo) {
    this.cupo = cupo;
  }

  public String getNivel() {
    return nivel;
  }

  public void setNivel(String nivel) {
    this.nivel = nivel;
  }
}
