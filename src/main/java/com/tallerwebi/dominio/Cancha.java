package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
@Table(name = "cancha")
public class Cancha {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  public Cancha() {}

  public Cancha(Long id, String nombre) {
    this.id = id;
    this.nombre = nombre;
  }

  // Getters y Setters...
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
}
