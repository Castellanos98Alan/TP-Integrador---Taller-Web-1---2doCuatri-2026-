package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.InvitacionAlPartidoException;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioInvitacionPartidoImpl implements ServicioInvitacionPartido {

  private final RepositorioPartido repositorioPartido;
  private final RepositorioUsuario repositorioUsuario;
  private final ServicioNotificacion servicioNotificacion;
  private final ServicioSolicitudAmistad servicioSolicitudAmistad;

  public ServicioInvitacionPartidoImpl(
    RepositorioPartido repositorioPartido,
    RepositorioUsuario repositorioUsuario,
    ServicioNotificacion servicioNotificacion,
    ServicioSolicitudAmistad servicioSolicitudAmistad
  ) {
    this.repositorioPartido = repositorioPartido;
    this.repositorioUsuario = repositorioUsuario;
    this.servicioNotificacion = servicioNotificacion;
    this.servicioSolicitudAmistad = servicioSolicitudAmistad;
  }

  @Override
  public void invitarJugador(Long idPartido, Long idJugador, Usuario usuarioQueInvita)
    throws InvitacionAlPartidoException {
    Partido partido = repositorioPartido.buscarPorId(idPartido);
    Usuario amigo = repositorioUsuario.buscarPorId(idJugador);

    validarDatosInvitacion(partido, amigo, usuarioQueInvita);
    validarPartidoActivo(partido);
    validarParticipacion(partido, usuarioQueInvita);
    validarAmistad(usuarioQueInvita, amigo);
    validarJugadorNoInscripto(partido, amigo);

    if (servicioNotificacion.existeInvitacionParaPartido(
            amigo.getId(),
            partido.getId())) {

      throw new InvitacionAlPartidoException(
              "Este amigo ya fue invitado a este partido"
      );
    }

    servicioNotificacion.notificarInvitacion(amigo, partido);
  }

  private void validarDatosInvitacion(Partido partido, Usuario amigo, Usuario usuarioQueInvita)
    throws InvitacionAlPartidoException {
    if (partido == null || amigo == null || usuarioQueInvita == null) {
      throw new InvitacionAlPartidoException("No se puede realizar la invitación");
    }
  }

  private void validarPartidoActivo(Partido partido) throws InvitacionAlPartidoException {
    if (partido.getEstado() != Partido.EstadoPartido.PENDIENTE) {
      throw new InvitacionAlPartidoException("El partido no admite invitaciones");
    }
  }

  private void validarParticipacion(Partido partido, Usuario usuarioQueInvita)
    throws InvitacionAlPartidoException {
    boolean participa = false;

    for (Usuario confirmado : partido.getConfirmados()) {
      if (confirmado.getId().equals(usuarioQueInvita.getId())) {
        participa = true;
        break;
      }
    }

    if (!participa) {
      throw new InvitacionAlPartidoException("Debes participar en el partido para invitar");
    }
  }

  private void validarAmistad(Usuario usuarioQueInvita, Usuario amigo)
    throws InvitacionAlPartidoException {
    if (!servicioSolicitudAmistad.sonAmigos(usuarioQueInvita, amigo)) {
      throw new InvitacionAlPartidoException("Solo puedes invitar a tus amigos");
    }
  }

  private void validarJugadorNoInscripto(Partido partido, Usuario amigo)
    throws InvitacionAlPartidoException {
    for (Usuario confirmado : partido.getConfirmados()) {
      if (confirmado.getId().equals(amigo.getId())) {
        throw new InvitacionAlPartidoException("Este amigo ya está en el partido");
      }
    }
  }
}
