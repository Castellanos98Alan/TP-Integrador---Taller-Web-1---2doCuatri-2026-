package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
@Table(name = "cancha")
public class Cancha {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private String provincia;
  private String localidad;

  public Cancha() {}

  public Cancha(Long id, String nombre, String provincia, String localidad) {
    this.id = id;
    this.nombre = nombre;
    this.provincia = provincia;
    this.localidad = localidad;
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

  public String getProvincia() {
    return this.provincia;
  }

  public void setProvincia(String provinciaNueva) {
    this.provincia = provinciaNueva;
  }

  public String getLocalidad() {
    return this.localidad;
  }

  public void setLocalidad(String localidadNueva) {
    this.localidad = localidadNueva;
  }
}
