package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Equipo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String nombre;

  /*@ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "equipo_jugador",
          joinColumns = @JoinColumn(name = "equipo_id"),
          inverseJoinColumns = @JoinColumn(name = "jugador_id")

  private List<Jugador> jugadores = new ArrayList<>();
 )*/
  public Equipo() {}

  /*public Equipo(Integer id, String nombre, List<Jugador> jugadores) {
    this.id = id;
    this.nombre = nombre;
    this.jugadores = jugadores;
  }

  public List<Jugador> getJugadores() {
    return jugadores;
  }

  public void setJugadores(List<Jugador> jugadores) {
    this.jugadores = jugadores;
  }*/
  public Equipo(Integer id, String nombre, List<Jugador> jugadores) {
    this.id = id;
    this.nombre = nombre;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
}
