package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
public class Jugador {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String nombre;

  private String posicion1;

  private String posicion2;

  private String nivel;

  @Column(nullable = false)
  private Integer puntos;

  public Jugador() {}

  public Jugador(Integer id, String nombre, String posicion1, String posicion2, String nivel) {
    this.id = id;
    this.nombre = nombre;
    this.posicion1 = posicion1;
    this.posicion2 = posicion2;
    this.nivel = nivel;
    this.puntos = 0;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getPosicion1() {
    return posicion1;
  }

  public void setPosicion1(String posicion) {
    this.posicion1 = posicion;
  }

  public String getPosicion2() {
    return posicion2;
  }

  public void setPosicion2(String posicion) {
    this.posicion2 = posicion;
  }

  public String getNivel() {
    return nivel;
  }

  public void setNivel(String nivel) {
    this.nivel = nivel;
  }

  public Integer getPuntos() {
    return puntos;
  }

  public void setPuntos(Integer puntos) {
    this.puntos = puntos;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
}
