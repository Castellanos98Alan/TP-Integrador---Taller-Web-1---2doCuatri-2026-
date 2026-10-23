package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Jugador;

public interface ServicioJugador {
  Jugador buscarPorUsuarioId(Long usuarioId);

  void guardar(Jugador jugador);

  void modificar(Jugador jugador);
}
