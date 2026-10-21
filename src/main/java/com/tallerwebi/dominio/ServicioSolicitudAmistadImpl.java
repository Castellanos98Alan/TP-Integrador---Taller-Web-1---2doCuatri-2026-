package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.SolicitudAmistadInvalida;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioSolicitudAmistadImpl implements ServicioSolicitudAmistad {

  private final RepositorioSolicitudAmistad repositorioSolicitudAmistad;

  public ServicioSolicitudAmistadImpl(RepositorioSolicitudAmistad repositorioSolicitudAmistad) {
    this.repositorioSolicitudAmistad = repositorioSolicitudAmistad;
  }

  @Override
  public void enviarSolicitud(Usuario emisor, Usuario receptor) {
    if (receptor.equals(emisor)) {
      throw new SolicitudAmistadInvalida("No se puede enviar solicitud a usted mismo");
    }

    if (repositorioSolicitudAmistad.existeSolicitudPendienteEntre(emisor, receptor)) {
      throw new SolicitudAmistadInvalida("Ya le envio una solicitud a esta persona");
    }

    SolicitudAmistad solicitud = new SolicitudAmistad();

    solicitud.setEmisor(emisor);
    solicitud.setReceptor(receptor);
    solicitud.setEstado(SolicitudAmistad.EstadoSolicitud.PENDIENTE);

    repositorioSolicitudAmistad.guardar(solicitud);
  }

  @Override
  public List<SolicitudAmistad> obtenerSolicitudesPendientes(Usuario receptor) {
    return repositorioSolicitudAmistad.buscarPendientesDe(receptor);
  }

  @Override
  public void aceptarSolicitud(Integer idSolicitud) {
    SolicitudAmistad solicitud = repositorioSolicitudAmistad.buscarPorId(idSolicitud);

    if (solicitud == null) {
      throw new SolicitudAmistadInvalida("La solicitud no existe");
    }

    solicitud.setEstado(SolicitudAmistad.EstadoSolicitud.ACEPTADA);

    repositorioSolicitudAmistad.modificar(solicitud);
  }

  @Override
  public void rechazarSolicitud(Integer idSolicitud) {
    SolicitudAmistad solicitud = repositorioSolicitudAmistad.buscarPorId(idSolicitud);

    if (solicitud == null) {
      throw new SolicitudAmistadInvalida("La solicitud no existe");
    }

    solicitud.setEstado(SolicitudAmistad.EstadoSolicitud.RECHAZADA);

    repositorioSolicitudAmistad.modificar(solicitud);
  }
}
