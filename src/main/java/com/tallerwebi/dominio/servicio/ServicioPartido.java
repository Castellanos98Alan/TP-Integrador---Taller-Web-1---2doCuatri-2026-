package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.InvitacionAlPartidoException;
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

  void invitarJugador(Long idPartido, Long idJugador, Usuario usuarioQueInvita)
    throws InvitacionAlPartidoException;
  List<Partido> buscarPartidosPorJugador(Long idJugador);
}
