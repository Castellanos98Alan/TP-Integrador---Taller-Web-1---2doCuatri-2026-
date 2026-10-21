package com.tallerwebi.dominio;

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
}
