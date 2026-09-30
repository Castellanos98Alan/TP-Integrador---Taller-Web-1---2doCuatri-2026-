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
    //Partidos harcodeados
    List<Partido> partidosFalsos = new ArrayList<>();
    Cancha cancha = new Cancha();
    cancha.setId(1L);
    cancha.setNombre("Cancha de fútbol los leones");

    Partido partido01 = new Partido();
    partido01.setId(1L);
    partido01.setEsPrivado(false);
    partido01.setNivel("PRINCIPIANTE");
    partido01.setcupoMaximo(22);
    partido01.setCancha(cancha);
    partido01.setCodigoDeAcceso("123");
    partido01.setDistanciaKm(5);
    LocalDateTime fechaFalsa = LocalDateTime.of(2026, 10, 2, 14, 30);
    partido01.setFecha(fechaFalsa);
    Partido partido02 = new Partido();

    partido02.setId(2L);
    partido02.setEsPrivado(true);
    partido02.setNivel("INTERMEDIO");
    partido02.setcupoMaximo(22);
    partido02.setCancha(cancha);
    LocalDateTime fechaFalsa2 = LocalDateTime.of(2026, 10, 2, 18, 00);
    partido02.setFecha(fechaFalsa2);
    partido02.setDistanciaKm(3);
    partidosFalsos.add(partido01);

    partidosFalsos.add(partido02);
    List<Partido> partidosFiltrados = new ArrayList<>();

    for (Partido p : partidosFalsos) {
      Boolean coincideNivel = FILTRO_TODOS.equals(filtroNivel) || filtroNivel.equals(p.getNivel());

      Boolean coincideTipo =
        FILTRO_TODOS.equals(filtroTipo) ||
        ("PUBLICO".equals(filtroTipo) && !p.getEsPrivado()) ||
        ("PRIVADO".equals(filtroTipo) && p.getEsPrivado());

      Boolean coincideCupo =
        FILTRO_TODOS.equals(filtrocupoMaximo) ||
        (p.getCupoMaximo() != null && p.getCupoMaximo().toString().equals(filtrocupoMaximo));

      Boolean coincideFecha =
        FILTRO_TODOS.equals(filtroFecha) ||
        (p.getFecha() != null && p.getFecha().toLocalDate().toString().equals(filtroFecha));

      Boolean coincideHora =
        FILTRO_TODOS.equals(filtroHora) ||
        (p.getFecha().toLocalTime() != null &&
          p.getFecha().toLocalTime().toString().equals(filtroHora));

      Boolean coincideDistancia =
        FILTRO_TODOS.equals(FiltroDistancia) ||
        (p.getDistanciaKm() != null && p.getDistanciaKm().toString().equals(FiltroDistancia));

      if (
        coincideNivel &&
        coincideTipo &&
        coincideCupo &&
        coincideFecha &&
        coincideHora &&
        coincideDistancia
      ) {
        partidosFiltrados.add(p);
      }
    }
    return partidosFiltrados;
  }
}
