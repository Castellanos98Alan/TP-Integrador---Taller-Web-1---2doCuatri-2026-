package com.tallerwebi.dominio;

import java.time.LocalDateTime;

public class Partido {

    public enum EstadoPartido {
        ACTIVO,
        FINALIZADO,
        CANCELADO
    }

    private Long id;
    private LocalDateTime fecha;
    private EstadoPartido estado;
    private String modalidad;
    private String nivel;
    private Integer cantJugadores;
    private String descripcion;
    private Usuario organizador;


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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
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