package com.tallerwebi.dominio;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioRankingImp implements ServicioRanking {

  private RepositorioJugador repositorioJugador;
  private static final Integer LIMITE_D = 10;
  private static final Integer LIMITE_C = 30;
  private static final Integer LIMITE_B = 50;
  private static final Integer LIMITE_A = 80;
  private static final Integer LIMITE_S = 110;

  public ServicioRankingImp(RepositorioJugador repositorioJugador) {
    this.repositorioJugador = repositorioJugador;
  }

  @Transactional
  @Override
  public List<Jugador> obtenerRanking() {
    return repositorioJugador.buscarRanking();
  }

  @Transactional
  @Override
  public List<Jugador> obtenerRankingFiltradoPorNivel(String nivel) {
    return repositorioJugador.buscarRankingPorNivel(nivel);
  }

  @Transactional
  @Override
  public List<Jugador> obtenerRankingFiltradoPorPosicion(String posicion) {
    return repositorioJugador.buscarRankingPorPosicion(posicion);
  }

  @Transactional
  @Override
  public List<Jugador> obtenerRankingFiltradoPorNivelYPosicion(String nivel, String posicion) {
    return repositorioJugador.buscarRankingPorPosicionYNivel(nivel, posicion);
  }

  @Override
  public String calcularNivel(Integer puntos) {
    if (puntos < LIMITE_D) {
      return "D";
    }

    if (puntos < LIMITE_C) {
      return "C";
    }

    if (puntos < LIMITE_B) {
      return "B";
    }

    if (puntos < LIMITE_A) {
      return "A";
    }

    if (puntos < LIMITE_S) {
      return "S";
    }

    return "SS";
  }
}
