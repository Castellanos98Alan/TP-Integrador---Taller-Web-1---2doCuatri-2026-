package com.tallerwebi.dominio;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioRankingImp implements ServicioRanking {

  private RepositorioUsuario repositorioUsuario;

  public ServicioRankingImp(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
  }

  @Transactional
  @Override
  public List<Usuario> obtenerRanking() {
    List<Usuario> jugadores = this.repositorioUsuario.buscarTodos();

    List<Usuario> ranking = new ArrayList<>(jugadores);

    ranking.sort((jugador1, jugador2) -> jugador2.getPuntos().compareTo(jugador1.getPuntos()));

    return ranking;
  }
}
