package com.tallerwebi.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "partido")
@SuppressWarnings("PMD.TooManyFields")
public class Partido {

  public enum EstadoPartido {
    ACTIVO,
    FINALIZADO,
    CANCELADO,
    PENDIENTE,
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nivel;
  private Boolean esPrivado = false;
  private LocalDateTime fecha;

  private Integer cupoMaximo;
  private String codigoDeAcceso;
  private Integer distancia;

  @Enumerated(EnumType.STRING)
  private EstadoPartido estado;

  private Integer cantJugadores;

  @ManyToOne(fetch = FetchType.EAGER)
  private Cancha cancha;

  // Relación con Organizador
  @ManyToOne(fetch = FetchType.EAGER)
  private Usuario organizador;

  // Lista de confirmados (Hibernate crea automáticamente la tabla intermedia partido_usuario)
  @ManyToMany(fetch = FetchType.LAZY)
  private List<Usuario> confirmados = new ArrayList<>();

  public Partido() {
    this.estado = EstadoPartido.ACTIVO;
  }

  // Getters, Setters y Métodos de negocio...
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNivel() {
    return nivel;
  }

  public void setNivel(String nivel) {
    this.nivel = nivel;
  }

  public Boolean getEsPrivado() {
    return esPrivado;
  }

  public void setEsPrivado(Boolean esPrivado) {
    this.esPrivado = esPrivado;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public void setFecha(LocalDateTime fecha) {
    this.fecha = fecha;
  }

  public Integer getCupoMaximo() {
    return cupoMaximo;
  }

  public void setCupoMaximo(Integer cupoMaximo) {
    this.cupoMaximo = cupoMaximo;
  }

  public String getCodigoDeAcceso() {
    return codigoDeAcceso;
  }

  public void setCodigoDeAcceso(String codigoDeAcceso) {
    this.codigoDeAcceso = codigoDeAcceso;
  }

  public Integer getDistancia() {
    return distancia;
  }

  public void setDistancia(Integer distancia) {
    this.distancia = distancia;
  }

  public EstadoPartido getEstado() {
    return estado;
  }

  public void setEstado(EstadoPartido estado) {
    this.estado = estado;
  }

  public Integer getCantJugadores() {
    return cantJugadores;
  }

  public void setCantJugadores(Integer cantJugadores) {
    this.cantJugadores = cantJugadores;
  }

  public Usuario getOrganizador() {
    return organizador;
  }

  public void setOrganizador(Usuario organizador) {
    this.organizador = organizador;
  }

  public Cancha getCancha() {
    return cancha;
  }

  public void setCancha(Cancha cancha) {
    this.cancha = cancha;
  }

  public List<Usuario> getConfirmados() {
    return confirmados;
  }

  public void setConfirmados(List<Usuario> confirmados) {
    this.confirmados = confirmados;
  }

  public void iniciarPartido() {
    this.estado = EstadoPartido.ACTIVO;
  }

  public void finalizarPartido() {
    this.estado = EstadoPartido.FINALIZADO;
  }

  public void cancelarPartido() {
    this.estado = EstadoPartido.CANCELADO;
  }

  public void setcupoMaximo(Integer nuevoTopeCupos) {
    this.cupoMaximo = nuevoTopeCupos;
  }

  public void setDistanciaKm(Integer distanciaNueva) {
    this.distancia = distanciaNueva;
  }

  public Integer getDistanciaKm() {
    return this.distancia;
  }
}
