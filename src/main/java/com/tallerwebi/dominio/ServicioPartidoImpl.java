package com.tallerwebi.dominio;

import com.tallerwebi.presentacion.DatosCrearPartido;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioPartido")
@Transactional
public class ServicioPartidoImpl implements ServicioPartido {

  private final RepositorioPartido repositorioPartido;

  @Autowired
  public ServicioPartidoImpl(RepositorioPartido repositorioPartido) {
    this.repositorioPartido = repositorioPartido;
  }

  @Override
  public void crearPartido(DatosCrearPartido datos, Usuario creador) {}

  @Override
  public void cancelarPartido(Long partidoId, Usuario usuarioLogueado) {
    Partido partido = repositorioPartido.buscarPorId(partidoId);

    if (partido == null) {
      throw new RuntimeException("El partido no existe.");
    }

    if (partido.getOrganizador() == null || !partido.getOrganizador().equals(usuarioLogueado)) {
      throw new RuntimeException("No estas autorizado para cancelar este partido.");
    }

    if (partido.getEstado() == Partido.EstadoPartido.FINALIZADO) {
      throw new RuntimeException("No se puede cancelar un partido que ya finalizo.");
    }

    partido.cancelarPartido();
    repositorioPartido.modificar(partido);
  }

  //@Override
  //public void crearPartido(DatosCrearPartido datos, Usuario creador) {
  // Lo hice para q no falle la inyección del contenedor Spring

  // mas adelante va a tener la funcion en el sistema de:
  // el partido en la base de datos
  // Controlar el cupo y los suplentes
  // Armado automático de equipos equilibrados
  // Carga y  validación del resultado final
  // }

  public static final String FILTRO_TODOS = "TODOS";

  @Override
  public List<Partido> listarPartidosSegunFiltro(
    String filtroNivel,
    String filtroTipo,
    String filtrocupoMaximo,
    String filtroFecha,
    String filtroHora,
    String FiltroDistancia
  ) {
    return repositorioPartido.buscarPartidosPorFiltro(
      filtroNivel,
      filtroTipo,
      filtrocupoMaximo,
      filtroFecha,
      filtroHora,
      FiltroDistancia
    );
  }
}
