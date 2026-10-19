package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
public class InscripcionPartido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partido_id")
  private Partido partido;

  @ManyToOne
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  @ManyToOne
  @JoinColumn(name = "equipo_id")
  private Equipo equipo;

  private Boolean confirmado = true;

  public InscripcionPartido() {}

  public InscripcionPartido(Partido partido, Usuario usuario) {
    this.partido = partido;
    this.usuario = usuario;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Partido getPartido() {
    return partido;
  }

  public void setPartido(Partido partido) {
    this.partido = partido;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Equipo getEquipo() {
    return equipo;
  }

  public void setEquipo(Equipo equipo) {
    this.equipo = equipo;
  }

  public Boolean getConfirmado() {
    return confirmado;
  }

  public void setConfirmado(Boolean confirmado) {
    this.confirmado = confirmado;
  }
}
