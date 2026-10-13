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
  private final RepositorioCancha repositorioCancha;

  @Autowired
  public ServicioPartidoImpl(
    RepositorioPartido repositorioPartido,
    RepositorioCancha repositorioCancha
  ) {
    this.repositorioPartido = repositorioPartido;
    this.repositorioCancha = repositorioCancha;
  }

  //spint 2
  @Override
  public void crearPartido(DatosCrearPartido datos, Usuario creador) {
    if (datos.getCupo() == null || datos.getCupo() <= 0) {
      throw new RuntimeException("El cupo debe ser mayor a cero.");
    }

    if (datos.getFechaHora() == null) {
      throw new RuntimeException("Debe ingresar una fecha y hora.");
    }

    Cancha cancha = repositorioCancha.buscarPorNombre(datos.getCancha());
    if (cancha == null) {
      throw new RuntimeException("La cancha ingresada no existe.");
    }

    Partido partido = new Partido();
    partido.setCancha(cancha);
    partido.setFecha(datos.getFechaHora());
    partido.setCupoMaximo(datos.getCupo());
    partido.setNivel(datos.getNivel());
    partido.setOrganizador(creador);

    repositorioPartido.guardar(partido);
  }

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
