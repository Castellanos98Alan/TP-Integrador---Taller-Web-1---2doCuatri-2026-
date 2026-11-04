package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Partido;
import java.util.List;

public interface ServicioPartidoFiltro {
  List<Partido> listarPartidosSegunFiltro(
    String filtroNivel,
    String filtroTipo,
    String cupoMaximo,
    String fecha,
    String filtroHora,
    String filtroPartido,
    String filtroLocalidad
  );

  List<String> obtenerFechasDisponibles();
  List<String> obtenerHorasDisponibles();
  List<String> obtenerProvinciasDisponibles();
  List<String> obtenerLocalidadesDisponibles();
  List<Integer> obtenerCuposDisponibles();
}
