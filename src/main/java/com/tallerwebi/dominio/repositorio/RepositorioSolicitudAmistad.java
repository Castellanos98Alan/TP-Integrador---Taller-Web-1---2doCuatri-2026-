package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.SolicitudAmistad;
import com.tallerwebi.dominio.Usuario;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioSolicitudAmistad {
  void guardar(SolicitudAmistad solicitud);

  List<SolicitudAmistad> buscarPendientesDe(Usuario receptor);

  void modificar(SolicitudAmistad solicitud);

  SolicitudAmistad buscarPorId(Integer id);

  boolean existeSolicitudPendienteEntre(Usuario emisor, Usuario receptor);

  Boolean sonAmigos(Usuario usuario1, Usuario usuario2);
}
