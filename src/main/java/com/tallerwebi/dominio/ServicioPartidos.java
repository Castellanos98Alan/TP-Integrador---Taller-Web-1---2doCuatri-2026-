package com.tallerwebi.dominio;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ServicioPartidos
 */
@FunctionalInterface
public interface ServicioPartidos {
  List<Partido> listarPartidosSegunFiltro(
    String filtroNivel,
    String filtroTipo,
    String cupoMaximo,
    String fecha,
    String filtroHora,
    String distancia
  );
}
