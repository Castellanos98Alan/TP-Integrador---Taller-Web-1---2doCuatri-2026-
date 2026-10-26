package com.tallerwebi.dominio;

//import jakarta.persistence.ManyToMany;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuppressWarnings("PMD.TooManyFields")
public class Partido {

  public enum EstadoPartido {
    ACTIVO,
    FINALIZADO,
    CANCELADO,
    PENDIENTE,
  }

  public enum EstadoEstadisticas {
    PENDIENTE_DE_CARGA,
    EN_REVISION,
    CONFIRMADA,
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private EstadoEstadisticas estadoEstadisticas = EstadoEstadisticas.PENDIENTE_DE_CARGA;

  private String nivel;
  private Boolean esPrivado = false;
  private LocalDateTime fecha;

  @ManyToOne(fetch = FetchType.EAGER)
  private Cancha cancha;

  private Integer cupoMaximo;
  private String codigoDeAcceso;
  private Integer distancia; // valor harcodeao. en realidad se obtiene de un calculo con entre las
  private Integer cantJugadores;

  // dos ubicaciones de usuario y Cancha.
  // hay que crear una entidad entre usuario y partido. ej Convocatoria.java
  // por ahora lo voy a dejar en ManyToMany
  // @ManyToMany

  @Enumerated(EnumType.STRING)
  private EstadoPartido estado;

  //private String modalidad;
  //private String descripcion;

  @ManyToOne(fetch = FetchType.EAGER)
  private Usuario organizador;

  //Cree InscripcionPartido como entidad intermedia para equipo, jugador y partido. Utilice OneToMany ya
  //que antes los confirmados era un ManyToMany. Cascade lo investigue y lo que hace es que te garantiza
  // que la base de datos se mantenga perfectamente sincronizada con las entidades, en este caso con partido.
  //orphanRemoval para poder borrar el espacio que ocupa en la base de datos, sin esto se eliminaria el jugador
  //pero seguiria ocupando espacio en la base de datos.
  @OneToMany(
    mappedBy = "partido",
    fetch = FetchType.EAGER,
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<InscripcionPartido> inscripciones = new ArrayList<>();

  @ManyToOne(fetch = FetchType.EAGER)
  private Equipo equipo1;

  @ManyToOne(fetch = FetchType.EAGER)
  private Equipo equipo2;

  @ManyToOne(fetch = FetchType.EAGER)
  private Resultado resultado;

  @OneToMany(fetch = FetchType.LAZY)
  private List<Estadistica> estadisticas;

  public Partido() {
    this.estado = EstadoPartido.ACTIVO;
  }

  public Partido(String nivel, Boolean esPrivado, LocalDateTime fecha) {
    this.nivel = nivel;
    this.esPrivado = esPrivado;
    this.fecha = fecha;
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Cancha getCancha() {
    return this.cancha;
  }

  public void setCancha(Cancha canchaNueva) {
    this.cancha = canchaNueva;
  }

  public Integer getCupoMaximo() {
    return this.cupoMaximo;
  }

  public void setCupoMaximo(Integer nuevoTopeCupos) {
    this.cupoMaximo = nuevoTopeCupos;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public void setFecha(LocalDateTime fecha) {
    this.fecha = fecha;
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

  public String getCodigoDeAcceso() {
    return this.codigoDeAcceso;
  }

  public void setCodigoDeAcceso(String codigoDeAcceso) {
    this.codigoDeAcceso = codigoDeAcceso;
  }

  public Integer getDistanciaKm() {
    return this.distancia;
  }

  public void setDistanciaKm(Integer distanciaNueva) {
    this.distancia = distanciaNueva;
  }

  public EstadoPartido getEstado() {
    return estado;
  }

  public void setEstado(EstadoPartido estado) {
    this.estado = estado;
  }

  public Usuario getOrganizador() {
    return organizador;
  }

  public void setOrganizador(Usuario organizador) {
    this.organizador = organizador;
  }

  public Integer getDistancia() {
    return distancia;
  }

  public void setDistancia(Integer distancia) {
    this.distancia = distancia;
  }

  public Equipo getEquipo1() {
    return equipo1;
  }

  public void setEquipo1(Equipo equipo1) {
    this.equipo1 = equipo1;
  }

  public Equipo getEquipo2() {
    return equipo2;
  }

  public void setEquipo2(Equipo equipo2) {
    this.equipo2 = equipo2;
  }

  public Resultado getResultado() {
    return resultado;
  }

  public void setResultado(Resultado resultado) {
    this.resultado = resultado;
  }

  public List<Estadistica> getEstadisticas() {
    return estadisticas;
  }

  public void setEstadisticas(List<Estadistica> estadisticas) {
    this.estadisticas = estadisticas;
  }

  public Integer getCantJugadores() {
    return cantJugadores;
  }

  public void setCantJugadores(Integer cantJugadores) {
    this.cantJugadores = cantJugadores;
  }

  public EstadoEstadisticas getEstadoEstadisticas() {
    return estadoEstadisticas;
  }

  public void setEstadoEstadisticas(EstadoEstadisticas estadoEstadisticas) {
    this.estadoEstadisticas = estadoEstadisticas;
  }

  //Este metodo se refactorizo debido a que anteriormente teniamos en partido una lista de jugadoresConfirmados.
  //Al crear la entidad intermedia InscripcionPartido cambiamos toda la logica, ahora los partidos tienen una lista
  //de Inscripciones, donde dentro estaran los jugadores.
  //Realizo un forEach para poder traer a los usuariosConfirmados que estan dentro de inscripcion.
  public List<Usuario> getConfirmados() {
    List<Usuario> usuariosConfirmados = new ArrayList<>();
    if (this.inscripciones != null) {
      for (InscripcionPartido inscripcion : this.inscripciones) {
        if (inscripcion.getUsuario() != null) {
          usuariosConfirmados.add(inscripcion.getUsuario());
        }
      }
    }
    return usuariosConfirmados;
  }

  public List<InscripcionPartido> getInscripciones() {
    return inscripciones;
  }

  public void setInscripciones(List<InscripcionPartido> inscripciones) {
    this.inscripciones = inscripciones;
  }
}
