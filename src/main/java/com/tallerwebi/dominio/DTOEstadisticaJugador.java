package com.tallerwebi.dominio;

public class DTOEstadisticaJugador {

    private Long idJugador; // O idUsuario, según lo que uses para identificar al jugador
    private Integer goles;
    private Integer amarillas;
    private Integer rojas;

    // Constructor vacío
    public DTOEstadisticaJugador() {
    }

    // Constructor con parámetros (opcional, pero útil)
    public DTOEstadisticaJugador(Long idJugador, Integer goles, Integer amarillas, Integer rojas) {
        this.idJugador = idJugador;
        this.goles = goles;
        this.amarillas = amarillas;
        this.rojas = rojas;
    }

    // Getters y Setters (¡Muy importantes para que Spring pueda leer los datos del formulario!)
    public Long getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(Long idJugador) {
        this.idJugador = idJugador;
    }

    public Integer getGoles() {
        return goles;
    }

    public void setGoles(Integer goles) {
        this.goles = goles;
    }

    public Integer getAmarillas() {
        return amarillas;
    }

    public void setAmarillas(Integer amarillas) {
        this.amarillas = amarillas;
    }

    public Integer getRojas() {
        return rojas;
    }

    public void setRojas(Integer rojas) {
        this.rojas = rojas;
    }
}
