package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.InscripcionPartido;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Partido.EstadoPartido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.repositorio.RepositorioCancha;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.presentacion.DatosCrearPartido;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de gestión de partidos.
 */
@Service("servicioPartido")
@Transactional
public class ServicioPartidoImpl implements ServicioPartido {

  private final RepositorioPartido repositorioPartido;
  private final RepositorioCancha repositorioCancha;
  private final RepositorioUsuario repositorioUsuario;
  private ServicioJugador servicioJugador;
  private ServicioNotificacion servicioNotificacion;
  private ServicioSolicitudAmistad servicioSolicitudAmistad;
  private ServicioInvitacionPartido servicioInvitacionPartido;
  private final ServicioBalanceoEquipo servicioBalanceoEquipo;
  private static final String MENSAJE_PARTIDO_NO_EXISTE = "El partido no existe.";
  private final ValidadorInscripcionPartido validadorInscripcionPartido;

  @Autowired
  public ServicioPartidoImpl(
    RepositorioPartido repositorioPartido,
    RepositorioCancha repositorioCancha,
    RepositorioUsuario repositorioUsuario,
    ServicioJugador servicioJugador,
    ServicioBalanceoEquipo servicioBalanceoEquipo,
    ServicioNotificacion servicioNotificacion,
    ServicioSolicitudAmistad servicioSolicitudAmistad,
    ServicioInvitacionPartido servicioInvitacionPartido,
    ValidadorInscripcionPartido validadorInscripcionPartido
  ) {
    this.repositorioCancha = repositorioCancha;
    this.repositorioPartido = repositorioPartido;
    this.repositorioUsuario = repositorioUsuario;
    this.servicioJugador = servicioJugador;
    this.servicioBalanceoEquipo = servicioBalanceoEquipo;
    this.servicioNotificacion = servicioNotificacion;
    this.servicioSolicitudAmistad = servicioSolicitudAmistad;
    this.servicioInvitacionPartido = servicioInvitacionPartido;
    this.validadorInscripcionPartido = validadorInscripcionPartido;
  }

  //cambiar los runtime a por excepcion por ser
  @Override
  public void crearPartido(DatosCrearPartido datos, Usuario creador) {
    if (datos.getCupo() == null || datos.getCupo() <= 0) {
      throw new CupoInvalidoException("El cupo debe ser mayor a cero.");
    }

    if (datos.getFechaHora() == null) {
      throw new FechaPartidoInvalidaException("Debe ingresar una fecha y hora.");
    }

    Cancha cancha = repositorioCancha.buscarPorNombre(datos.getCancha());

    if (cancha == null) {
      throw new CanchaNoEncontradaException("La cancha ingresada no existe.");
    }

    validarCanchaDisponible(cancha, datos.getFechaHora());

    Partido partido = new Partido();

    partido.setCancha(cancha);
    partido.setFecha(datos.getFechaHora());
    partido.setCupoMaximo(datos.getCupo());
    partido.setNivel(datos.getNivel());
    partido.setOrganizador(creador);
    partido.setEsPrivado(datos.getEsPrivado());

    if (Boolean.TRUE.equals(datos.getEsPrivado())) {
      partido.setCodigoDeAcceso(obtenerCodigoParaPartido(datos.getCodigoDeAcceso()));
    }

    repositorioPartido.guardar(partido);
  }

  private String obtenerCodigoParaPartido(String codigoIngresado) {
    if (codigoIngresado == null || codigoIngresado.isBlank()) {
      return generarCodigoAcceso();
    }

    return codigoIngresado.trim().toUpperCase(Locale.ROOT);
  }

  @Override
  public String generarCodigoAcceso() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
  }

  private void validarCanchaDisponible(Cancha cancha, LocalDateTime fechaHora) {
    LocalDateTime inicioVentana = fechaHora.minusHours(1);
    LocalDateTime finVentana = fechaHora.plusHours(1);

    List<Partido> partidosEnLaCancha = repositorioPartido.buscarPartidosEnRango(
      cancha.getId(),
      inicioVentana,
      finVentana
    );

    if (!partidosEnLaCancha.isEmpty()) {
      throw new CanchaNoDisponibleException(
        "La cancha no está disponible en esa franja horaria (los partidos duran 1 hora)."
      );
    }
  }

  @Override
  public List<Cancha> listarCanchas() {
    return repositorioCancha.buscarTodas();
  }

  @Override
  public List<String> listarLocalidades() {
    return repositorioCancha.buscarLocalidades();
  }

  @Override
  public void cancelarPartido(Long partidoId, Usuario usuarioLogueado) {
    Partido partido = obtenerPartidoValidado(partidoId);

    validarOrganizador(partido, usuarioLogueado, "No estas autorizado para cancelar este partido.");

    if (partido.getEstado() == Partido.EstadoPartido.FINALIZADO) {
      throw new RuntimeException("No se puede cancelar un partido que ya finalizo.");
    }

    partido.cancelarPartido();
    repositorioPartido.modificar(partido);

    partido
      .getConfirmados()
      .forEach(jugador -> servicioNotificacion.notificarCancelacion(jugador, partido));
  }

  //Utilice el Transactional para que no se me cierre la sesion al momento de ejecutar el metodo,
  //con esto puedo consultar correctamente el partido mandandole el id por parametro.
  //Utilizo el repositorioPartido que ya tiene un metodo que es buscarPorId.
  @Transactional
  @Override
  public Partido buscarPartidoPorId(Long id) {
    return repositorioPartido.buscarPorId(id);
  }

  //Primero utilice directamente el metodo buscarPartidoPorId en lugar de usar el repositorioPartido directamente
  //para que cada vez que lo use me traiga el partido con su lista de confirmados inicializada. Tambien hice una validacion
  //rapida para ver si el partido existe.
  //Por ultimo decidi separar las validaciones en metodos privados debido a la regla de PMD y que quede mejor organizado..

  @Override
  public void unirseAPartido(Long partidoId, Jugador usuarioLogueadoJugador) {
    unirseAPartido(partidoId, usuarioLogueadoJugador, null);
  }

  @Override
  public void unirseAPartido(Long partidoId, Jugador usuarioLogueadoJugador, String codigoAcceso) {
    Partido partido = buscarPartidoPorId(partidoId);

    if (partido == null) {
      throw new PartidoNoEncontrado("El partido " + partidoId + " no existe.");
    }

    validarCodigoDeAcceso(partido, usuarioLogueadoJugador, codigoAcceso);

    validadorInscripcionPartido.validarInscripcion(partido, usuarioLogueadoJugador);

    InscripcionPartido nuevaInscripcion = new InscripcionPartido(partido, usuarioLogueadoJugador);

    nuevaInscripcion.setFechaInscripcion(LocalDateTime.now());

    partido.getInscripciones().add(nuevaInscripcion);
    partido.setCantJugadores(partido.getInscripciones().size());

    int cupoMaximo = partido.getCupoMaximo();

    if (partido.getCantJugadores() == cupoMaximo) {
      servicioBalanceoEquipo.balancearEquipos(partido);
      servicioBalanceoEquipo.asignarCapitanes(partido);
      partido.setEstado(EstadoPartido.ACTIVO);
    }

    repositorioPartido.modificar(partido);
  }

  private void validarCodigoDeAcceso(Partido partido, Jugador jugador, String codigoAcceso) {
    Usuario usuario = jugador.getUsuario();

    ControlAccesoPartido.validarCodigo(
      partido,
      usuario,
      codigoAcceso,
      () -> servicioNotificacion.existeInvitacionParaPartido(usuario.getId(), partido.getId())
    );
  }

  @Override
  public boolean puedeAccederAPartido(Long partidoId, Usuario usuario) {
    Partido partido = buscarPartidoPorId(partidoId);

    if (partido == null) {
      throw new PartidoNoEncontrado("El partido " + partidoId + " no existe.");
    }

    return ControlAccesoPartido.tieneAcceso(
      partido,
      usuario,
      () ->
        usuario != null &&
        servicioNotificacion.existeInvitacionParaPartido(usuario.getId(), partido.getId())
    );
  }

  @Override
  public void invitarJugador(Long idPartido, Long idJugador, Usuario usuarioQueInvita)
    throws InvitacionAlPartidoException {
    servicioInvitacionPartido.invitarJugador(idPartido, idJugador, usuarioQueInvita);
  }

  @Override
  public List<Partido> buscarPartidosPorJugador(Long idJugador) {
    return repositorioPartido.buscarPartidosPorJugador(idJugador);
  }

  @Override
  public void abandonarPartido(Long partidoId, Jugador jugador) {
    Partido partido = obtenerPartidoValidado(partidoId);
    validarPartidoModificable(
      partido,
      "No puedes abandonar un partido que ya está en curso o finalizado."
    );
    InscripcionPartido inscripcion = obtenerInscripcionJugador(
      partido,
      jugador.getId(),
      "No estás inscripto en este partido."
    );
    removerInscripcionYGuardar(partido, inscripcion);
  }

  @Override
  public void expulsarJugador(Long partidoId, Long idJugadorAExpulsar, Usuario organizador) {
    Partido partido = obtenerPartidoValidado(partidoId);

    validarOrganizador(partido, organizador, "Solo el organizador puede expulsar jugadores.");

    validarPartidoModificable(
      partido,
      "No puedes expulsar jugadores de un partido que ya está en curso o finalizado."
    );
    InscripcionPartido inscripcion = obtenerInscripcionJugador(
      partido,
      idJugadorAExpulsar,
      "El jugador no está inscripto en este partido."
    );
    removerInscripcionYGuardar(partido, inscripcion);
  }

  @Override
  public String generarCodigoAcceso() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
  }

  private Partido obtenerPartidoValidado(Long partidoId) {
    Partido partido = buscarPartidoPorId(partidoId);
    if (partido == null) throw new RuntimeException(MENSAJE_PARTIDO_NO_EXISTE);
    return partido;
  }

  private void validarOrganizador(Partido partido, Usuario organizador, String mensajeError) {
    if (
      partido.getOrganizador() == null ||
      !partido.getOrganizador().getId().equals(organizador.getId())
    ) {
      throw new RuntimeException(mensajeError);
    }
  }

  private void validarPartidoModificable(Partido partido, String mensajeError) {
    if (partido.getEstado() != EstadoPartido.PENDIENTE) {
      throw new RuntimeException(mensajeError);
    }
  }

  private InscripcionPartido obtenerInscripcionJugador(
    Partido partido,
    Long idJugador,
    String mensajeError
  ) {
    if (partido.getInscripciones() == null) throw new RuntimeException(mensajeError);
    return partido
      .getInscripciones()
      .stream()
      .filter(i -> i.getJugador().getId().equals(idJugador))
      .findFirst()
      .orElseThrow(() -> new RuntimeException(mensajeError));
  }

  private void removerInscripcionYGuardar(Partido partido, InscripcionPartido inscripcion) {
    partido.getInscripciones().remove(inscripcion);
    partido.setCantJugadores(partido.getInscripciones().size());
    repositorioPartido.modificar(partido);
  }
}
