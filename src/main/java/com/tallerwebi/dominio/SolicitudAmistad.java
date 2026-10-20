package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
public class SolicitudAmistad {

  public enum EstadoSolicitud {
    PENDIENTE,
    RECHAZADA,
    ACEPTADA,
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne
  private Usuario emisor;

  @ManyToOne
  private Usuario receptor;

  @Enumerated(EnumType.STRING)
  private EstadoSolicitud estado;

  public SolicitudAmistad() {}

  public SolicitudAmistad(Usuario emisor, Usuario receptor, EstadoSolicitud estado) {
    this.emisor = emisor;
    this.receptor = receptor;
    this.estado = estado;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Usuario getEmisor() {
    return emisor;
  }

  public void setEmisor(Usuario emisor) {
    this.emisor = emisor;
  }

  public Usuario getReceptor() {
    return receptor;
  }

  public void setReceptor(Usuario receptor) {
    this.receptor = receptor;
  }

  public EstadoSolicitud getEstado() {
    return estado;
  }

  public void setEstado(EstadoSolicitud estado) {
    this.estado = estado;
  }
}
