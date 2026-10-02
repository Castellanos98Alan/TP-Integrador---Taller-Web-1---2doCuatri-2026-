package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Jugador extends Usuario {

  private String posicion1;
  private String posicion2;
  private String nivel;

  @ManyToMany(fetch = FetchType.LAZY)
  private List<Equipo> equipos = new ArrayList<>();

  public Jugador() {}

  public Jugador(String posicion1, String posicion2, String nivel) {
    this.posicion1 = posicion1;
    this.posicion2 = posicion2;
    this.nivel = nivel;
  }

  // Getters y Setters...
  public String getPosicion1() {
    return posicion1;
  }

  public void setPosicion1(String posicion1) {
    this.posicion1 = posicion1;
  }

  public String getPosicion2() {
    return posicion2;
  }

  public void setPosicion2(String posicion2) {
    this.posicion2 = posicion2;
  }

  public String getNivel() {
    return nivel;
  }

  public void setNivel(String nivel) {
    this.nivel = nivel;
  }

  public List<Equipo> getEquipos() {
    return equipos;
  }

  public void setEquipos(List<Equipo> equipos) {
    this.equipos = equipos;
  }
}
