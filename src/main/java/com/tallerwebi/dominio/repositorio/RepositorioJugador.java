package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.Jugador;
import java.util.List;

public interface RepositorioJugador {
  void guardar(Jugador jugador);

  List<Jugador> buscarTodos();

  List<Jugador> buscarRanking();

  Jugador buscarPorUsuarioId(Long usuarioId);

  Jugador buscarPorId(Long idJugador);

  void modificar(Jugador jugador);

  List<Jugador> buscarRankingFiltrado(
    String nivel,
    String posicion,
    String rango,
    String vista,
    Long usuarioId
  );
}
