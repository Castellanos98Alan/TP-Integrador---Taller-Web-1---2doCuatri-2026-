package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enumeration.TipoDeNotificacion;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notificacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Usuario usuario;

  private String mensaje;

  @Enumerated(EnumType.STRING)
  private TipoDeNotificacion tipoDeNotificacion;

  private Boolean leida;

  private LocalDateTime fecha;

  @ManyToOne
  private Partido partido;

  public TipoDeNotificacion getTipoDeNotificacion() {
    return tipoDeNotificacion;
  }

  public void setTipoDeNotificacion(TipoDeNotificacion tipoDeNotificacion) {
    this.tipoDeNotificacion = tipoDeNotificacion;
  }

  public Boolean getLeida() {
    return leida;
  }

  public void setLeida(Boolean leida) {
    this.leida = leida;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public void setFecha(LocalDateTime fecha) {
    this.fecha = fecha;
  }

  public Partido getPartido() {
    return partido;
  }

  public void setPartido(Partido partido) {
    this.partido = partido;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }
}
