package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipo")
public class Equipo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // Cambiado a Long para consistencia con el resto

  private String nombre;

  //@ManyToMany(mappedBy = "equipos", fetch = FetchType.LAZY)

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "equipo_jugador",
    joinColumns = @JoinColumn(name = "equipo_id"),
    inverseJoinColumns = @JoinColumn(name = "jugador_id")
  )
  private List<Jugador> jugadores = new ArrayList<>();

  public Equipo() {}

  public Equipo(Long id, String nombre) {
    this.id = id;
    this.nombre = nombre;
  }

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

  public List<Jugador> getJugadores() {
    return jugadores;
  }

  public void setJugadores(List<Jugador> jugadores) {
    this.jugadores = jugadores;
  }
}
