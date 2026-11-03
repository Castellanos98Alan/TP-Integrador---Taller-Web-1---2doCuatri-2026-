package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import java.util.List;

public interface ServicioNotificacion {
  void notificarInvitacion(Usuario usuario, Partido partido);

  void notificarFormularioCompletado(Usuario capitan, Partido partido);

  void notificarCancelacion(Usuario usuario, Partido partido);

  List<Notificacion> obtenerNotificaciones(Long idUsuario);

  void marcarComoLeida(Long idNotificacion);

  List<Notificacion> obtenerNotificacionesNoLeidas(Long idUsuario);

  boolean existeInvitacionParaPartido(
          Long idUsuario,
          Long idPartido
  );
}
