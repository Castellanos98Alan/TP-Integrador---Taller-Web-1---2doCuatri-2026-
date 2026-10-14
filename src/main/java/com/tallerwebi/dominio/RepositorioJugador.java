package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioJugador {
  void guardar(Jugador jugador);
  List<Jugador> buscarTodos();
  List<Jugador> buscarRanking();

  List<Jugador> buscarRankingPorNivel(String nivel);

  List<Jugador> buscarRankingPorPosicion(String posicion);

  List<Jugador> buscarRankingPorPosicionYNivel(String nivel, String posicion);

  Jugador buscarPorUsuarioId(Long usuarioId);

  void modificar(Jugador jugador);
}
