package com.tallerwebi.dominio;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioRankingImp implements ServicioRanking {

  private RepositorioJugador repositorioJugador;

  public ServicioRankingImp(RepositorioJugador repositorioJugador) {
    this.repositorioJugador = repositorioJugador;
  }

  @Transactional
  @Override
  public List<Jugador> obtenerRanking() {
    List<Jugador> jugadores = this.repositorioJugador.buscarTodos();

    List<Jugador> ranking = new ArrayList<>(jugadores);

    ranking.sort((jugador1, jugador2) -> jugador2.getPuntos().compareTo(jugador1.getPuntos()));

    return ranking;
  }
}
