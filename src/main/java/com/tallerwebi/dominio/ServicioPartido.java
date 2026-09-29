package com.tallerwebi.dominio;

import com.tallerwebi.presentacion.DatosCrearPartido;
import java.util.List;

//@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface ServicioPartido {
  void crearPartido(DatosCrearPartido datos, Usuario creador);
  void cancelarPartido(Long partidoId, Usuario usuarioLogueado);

  List<Partido> listarPartidosSegunFiltro(
    String filtroNivel,
    String filtroTipo,
    String cupoMaximo,
    String fecha,
    String filtroHora,
    String distancia
  );
}
