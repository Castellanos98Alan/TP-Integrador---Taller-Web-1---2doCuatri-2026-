package com.tallerwebi.dominio;

//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.ManyToMany;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Partido
 */
//@Entity
public class Partido {
  public enum EstadoPartido {
        ACTIVO,
        FINALIZADO,
        CANCELADO
    }

  // @Id
  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nivel;
  private Boolean esPrivado = false;
  private LocalDateTime fecha;

  // @ManyToOne
  private Cancha cancha;
  private Integer cupoMaximo;
  private String codigoDeAcceso;
  private Integer distancia; // valor harcodeao. en realidad se obtiene de un calculo con entre las dos ubicaciones de usuario y Cancha.
  // hay que crear una entidad entre usuario y partido. ej Convocatoria.java
  // por ahora lo voy a dejar en ManyToMany
  // @ManyToMany
  
  private EstadoPartido estado;
    //private String modalidad;
    //private String nivel;
    private Integer cantJugadores;
    //private String descripcion;
    private Usuario organizador;

  
  private List<Usuario> confirmados = new ArrayList<>();
  
   public Partido() {
        this.estado = EstadoPartido.ACTIVO;
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

  public void setcupoMaximo(Integer nuevoTopeCupos) {
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

  public List<Usuario> getConfirmados() {
    return confirmados;
  }

  public void setConfirmados(List<Usuario> confirmados) {
    this.confirmados = confirmados;
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

    public Integer getCantJugadores() {
        return cantJugadores;
    }

    public void setCantJugadores(Integer cantJugadores) {
        this.cantJugadores = cantJugadores;
    }
  
}

