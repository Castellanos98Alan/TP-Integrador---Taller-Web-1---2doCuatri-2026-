package com.tallerwebi.dominio;
import com.tallerwebi.presentacion.DatosCrearPartido;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface ServicioPartido {
    void crearPartido(DatosCrearPartido datos, Usuario creador);
    void cancelarPartido(Long partidoId, Usuario usuarioLogueado);
}


