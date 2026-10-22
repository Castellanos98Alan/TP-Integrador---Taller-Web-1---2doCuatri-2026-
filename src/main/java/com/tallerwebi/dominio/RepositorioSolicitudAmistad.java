package com.tallerwebi.dominio;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioSolicitudAmistad {
  void guardar(SolicitudAmistad solicitud);

  List<SolicitudAmistad> buscarPendientesDe(Usuario receptor);

  void modificar(SolicitudAmistad solicitud);

  SolicitudAmistad buscarPorId(Integer id);

  boolean existeSolicitudPendienteEntre(Usuario emisor, Usuario receptor);
}
