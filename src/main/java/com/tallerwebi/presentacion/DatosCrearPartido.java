package com.tallerwebi.presentacion;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class DatosCrearPartido {

  private String cancha;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime fechaHora;

  private Integer cupo;
  private String nivel;
  private String localidad;
  private Boolean esPrivado = false;
  private String codigoDeAcceso;

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

  public String getLocalidad() {
    return localidad;
  }

  public void setLocalidad(String localidad) {
    this.localidad = localidad;
  }

  public Boolean getEsPrivado() {
    return esPrivado;
  }

  public void setEsPrivado(Boolean esPrivado) {
    this.esPrivado = esPrivado;
  }

  public String getCodigoDeAcceso() {
    return codigoDeAcceso;
  }

  public void setCodigoDeAcceso(String codigoDeAcceso) {
    this.codigoDeAcceso = codigoDeAcceso;
  }
}
