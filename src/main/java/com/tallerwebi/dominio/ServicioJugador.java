package com.tallerwebi.dominio;

public interface ServicioJugador {
  Jugador buscarPorUsuarioId(Long usuarioId);

  void guardar(Jugador jugador);

  void modificar(Jugador jugador);
}
