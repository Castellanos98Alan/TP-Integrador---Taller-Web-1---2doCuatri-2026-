package com.tallerwebi.dominio;

@FunctionalInterface
public interface ServicioPartido {
    void cancelarPartido(Long partidoId, Usuario usuarioLogueado);
}