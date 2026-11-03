package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Enumeration.TipoDeNotificacion;
import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioNotificacion;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioNotificationImpl implements ServicioNotificacion {

  private final RepositorioNotificacion repositorioNotificacion;
  private final RepositorioPartido repositorioPartido;
  private final RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioNotificationImpl(
    RepositorioNotificacion repositorioNotificacion,
    RepositorioPartido repositorioPartido,
    RepositorioUsuario repositorioUsuario
  ) {
    this.repositorioNotificacion = repositorioNotificacion;
    this.repositorioPartido = repositorioPartido;
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public void notificarInvitacion(Usuario usuario, Partido partido) {
    Notificacion notificacion = new Notificacion();

    notificacion.setUsuario(usuario);
    notificacion.setPartido(partido);
    notificacion.setTipoDeNotificacion(TipoDeNotificacion.INVITACION_PARTIDO);
    notificacion.setMensaje("Fuiste invitado a un partido");
    notificacion.setLeida(false);
    notificacion.setFecha(LocalDateTime.now());

    repositorioNotificacion.guardar(notificacion);
  }

  @Override
  public void notificarFormularioCompletado(Usuario capitan, Partido partido) {
    Notificacion notificacion = new Notificacion();

    notificacion.setUsuario(capitan);
    notificacion.setPartido(partido);
    notificacion.setTipoDeNotificacion(TipoDeNotificacion.FORMULARIO_COMPLETADO);
    notificacion.setMensaje("El formulario fue completado, puede ingresar a ver los detalles");
    notificacion.setLeida(false);
    notificacion.setFecha(LocalDateTime.now());

    repositorioNotificacion.guardar(notificacion);
  }

  @Override
  public void notificarCancelacion(Usuario usuario, Partido partido) {
    Notificacion notificacion = new Notificacion();

    notificacion.setUsuario(usuario);
    notificacion.setPartido(partido);
    notificacion.setTipoDeNotificacion(TipoDeNotificacion.PARTIDO_CANCELADO);
    notificacion.setMensaje("El partido al cual se anoto fue cancelado");
    notificacion.setLeida(false);
    notificacion.setFecha(LocalDateTime.now());

    repositorioNotificacion.guardar(notificacion);
  }

  @Override
  public List<Notificacion> obtenerNotificaciones(Long idUsuario) {
    return repositorioNotificacion.buscarPorUsuario(idUsuario);
  }

  @Override
  public void marcarComoLeida(Long idNotificacion) {
    Notificacion notificacion = repositorioNotificacion.buscarPorID(idNotificacion);

    if (notificacion != null) {
      notificacion.setLeida(true);
    }
  }

  @Override
  public List<Notificacion> obtenerNotificacionesNoLeidas(Long idUsuario) {
    return repositorioNotificacion.buscarNoLeidasPorUsuario(idUsuario);
  }

  @Override
  public boolean existeInvitacionParaPartido(
          Long idUsuario,
          Long idPartido) {

    return repositorioNotificacion.existeInvitacionParaPartido(
            idUsuario,
            idPartido
    );
  }
}
