package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.InvitacionAlPartidoException;

@FunctionalInterface
public interface ServicioInvitacionPartido {
  void invitarJugador(Long idPartido, Long idJugador, Usuario usuarioQueInvita)
    throws InvitacionAlPartidoException;
}
