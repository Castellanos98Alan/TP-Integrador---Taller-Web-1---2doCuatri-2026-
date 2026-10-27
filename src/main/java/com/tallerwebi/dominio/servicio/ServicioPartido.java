package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.presentacion.DatosCrearPartido;
import java.util.List;

// @SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface ServicioPartido {
  void crearPartido(DatosCrearPartido datos, Usuario creador);
  void cancelarPartido(Long partidoId, Usuario usuarioLogueado);
  void unirseAPartido(Long partidoId, Jugador usuarioLogueadoJugador);

  Partido buscarPartidoPorId(Long id);
  List<Cancha> listarCanchas(); // NUEVO
  List<String> listarLocalidades(); // NUEVO

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
