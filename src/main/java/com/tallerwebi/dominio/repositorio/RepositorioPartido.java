package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.Partido;
import java.util.List;

public interface RepositorioPartido {
  Partido buscarPorId(Long id);
  List<Partido> buscarPartidosPorFiltro(
    String filtroNivel,
    String filtroTipo,
    String filtroCupoMaximo,
    String filtroFecha,
    String filtroHora,
    String filtroProvincia,
    String filtroLocalidad
  );

  void guardar(Partido partido);
  void modificar(Partido partido);
  List<Partido> buscarPartidosPorJugador(Long idJugador);
}
