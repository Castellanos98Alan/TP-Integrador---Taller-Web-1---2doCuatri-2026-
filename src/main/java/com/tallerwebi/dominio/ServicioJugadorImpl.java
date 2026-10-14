package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioJugadorImpl implements ServicioJugador {

  private final RepositorioJugador repositorioJugador;

  @Autowired
  public ServicioJugadorImpl(RepositorioJugador repositorioJugador) {
    this.repositorioJugador = repositorioJugador;
  }

  @Override
  public Jugador buscarPorUsuarioId(Long usuarioId) {
    return repositorioJugador.buscarPorUsuarioId(usuarioId);
  }

  @Override
  public void guardar(Jugador jugador) {
    repositorioJugador.guardar(jugador);
  }

  @Override
  public void modificar(Jugador jugador) {
    repositorioJugador.modificar(jugador);
  }
}
