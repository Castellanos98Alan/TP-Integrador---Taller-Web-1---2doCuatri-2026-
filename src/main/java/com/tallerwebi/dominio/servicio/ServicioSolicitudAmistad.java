package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.SolicitudAmistad;
import com.tallerwebi.dominio.Usuario;
import java.util.List;

public interface ServicioSolicitudAmistad {
  void enviarSolicitud(Usuario emisor, Usuario receptor);

  List<SolicitudAmistad> obtenerSolicitudesPendientes(Usuario receptor);

  void aceptarSolicitud(Integer idSolicitud);

  void rechazarSolicitud(Integer idSolicitud);
}
