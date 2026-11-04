package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.Notificacion;
import java.util.List;

public interface RepositorioNotificacion {
  void guardar(Notificacion notificacion);

  List<Notificacion> buscarPorUsuario(Long idUsuario);

  Notificacion buscarPorID(Long id);

  List<Notificacion> buscarNoLeidasPorUsuario(Long idUsuario);

  boolean existeInvitacionParaPartido(Long idUsuario, Long idPartido);
}
