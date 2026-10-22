package com.tallerwebi.dominio;

import jakarta.persistence.*;

@SuppressWarnings("PMD.TooManyFields")
@Entity
public class Jugador {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer partidosJugados;
  private Integer partidosGanados;
  private Integer partidosPerdidos;

  private String nombre;

  private String posicion1;

  private String posicion2;

  private String nivel;

  @OneToOne(fetch = FetchType.EAGER)
  private Usuario usuario;

  @Column(nullable = false)
  private Integer puntos;

  // NUEVO: ruta de la foto guardada en el proyecto
  private String foto;

  // NUEVO: posición del encuadre de la foto
  private Integer fotoPosicionX = 50;
  private Integer fotoPosicionY = 50;

  public Jugador() {}

  public Jugador(String nombre, String posicion1, String posicion2, String nivel) {
    this.nombre = nombre;
    this.posicion1 = posicion1;
    this.posicion2 = posicion2;
    this.nivel = nivel;
    this.puntos = 0;
    this.partidosJugados = 0;
    this.partidosGanados = 0;
    this.partidosPerdidos = 0;
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

  public Integer getPartidosJugados() {
    return partidosJugados;
  }

  public Integer getPartidosGanados() {
    return partidosGanados;
  }

  public Integer getPartidosPerdidos() {
    return partidosPerdidos;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setPartidosJugados(Integer partidosJugados) {
    this.partidosJugados = partidosJugados;
  }

  public void setPartidosGanados(Integer partidosGanados) {
    this.partidosGanados = partidosGanados;
  }

  public void setPartidosPerdidos(Integer partidosPerdidos) {
    this.partidosPerdidos = partidosPerdidos;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public String getFoto() {
    return foto;
  }

  public void setFoto(String foto) {
    this.foto = foto;
  }

  public Integer getFotoPosicionX() {
    return fotoPosicionX != null ? fotoPosicionX : 50;
  }

  public void setFotoPosicionX(Integer fotoPosicionX) {
    this.fotoPosicionX = fotoPosicionX;
  }

  public Integer getFotoPosicionY() {
    return fotoPosicionY != null ? fotoPosicionY : 50;
  }

  public void setFotoPosicionY(Integer fotoPosicionY) {
    this.fotoPosicionY = fotoPosicionY;
  }
}
