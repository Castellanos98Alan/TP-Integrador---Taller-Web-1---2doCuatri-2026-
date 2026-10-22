package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioSolicitudAmistad {
  void enviarSolicitud(Usuario emisor, Usuario receptor);

  List<SolicitudAmistad> obtenerSolicitudesPendientes(Usuario receptor);

  void aceptarSolicitud(Integer idSolicitud);

  void rechazarSolicitud(Integer idSolicitud);
}
