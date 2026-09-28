package com.tallerwebi.presentacion;

public class DatosCrearPartido {

  private String cancha;
  private String fechaHora;
  private Integer cupo;
  private String nivel;

  public DatosCrearPartido() {}

  public DatosCrearPartido(String cancha, String fechaHora, Integer cupo, String nivel) {
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

  public String getFechaHora() {
    return fechaHora;
  }

  public void setFechaHora(String fechaHora) {
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
