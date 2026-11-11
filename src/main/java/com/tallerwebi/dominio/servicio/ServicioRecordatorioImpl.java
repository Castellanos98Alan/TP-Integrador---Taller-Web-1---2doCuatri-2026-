package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.InscripcionPartido;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioRecordatorioImpl implements ServicioRecordatorio {

  private final RepositorioPartido repositorioPartido;
  private final ServicioEmail servicioEmail;

  public ServicioRecordatorioImpl(
    RepositorioPartido repositorioPartido,
    ServicioEmail servicioEmail
  ) {
    this.repositorioPartido = repositorioPartido;
    this.servicioEmail = servicioEmail;
  }

  @Override
  @Transactional
  public void enviarRecordatoriosPendientes(LocalDateTime ahora) {
    LocalDateTime inicio = ahora.plusHours(3);
    LocalDateTime fin = inicio.plusMinutes(1);

    List<Partido> partidos = repositorioPartido.buscarPartidosParaRecordatorio(inicio, fin);

    for (Partido partido : partidos) {
      procesarPartido(partido);
    }
  }

  private void procesarPartido(Partido partido) {
    if (
      partido.getEstado() == Partido.EstadoPartido.CANCELADO ||
      partido.getEstado() == Partido.EstadoPartido.FINALIZADO
    ) {
      return;
    }

    for (InscripcionPartido inscripcion : partido.getInscripciones()) {
      procesarInscripcion(partido, inscripcion);
    }
  }

  private void procesarInscripcion(Partido partido, InscripcionPartido inscripcion) {
    if (!Boolean.TRUE.equals(inscripcion.getConfirmado())) {
      return;
    }

    if (Boolean.TRUE.equals(inscripcion.getRecordatorioEnviado())) {
      return;
    }

    if (inscripcion.getJugador() == null) {
      return;
    }

    Usuario usuario = inscripcion.getJugador().getUsuario();

    if (!tieneEmailValido(usuario)) {
      return;
    }

    enviarRecordatorio(partido, usuario);
    inscripcion.setRecordatorioEnviado(true);
  }

  private boolean tieneEmailValido(Usuario usuario) {
    return usuario != null && usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty();
  }

  private void enviarRecordatorio(Partido partido, Usuario usuario) {
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");

    String horario = partido.getFecha().format(formato);

    String llegada = partido.getFecha().minusMinutes(10).format(formato);

    String asunto = "Hoy tenes partido - ArmoLaCancha";

    String mensaje =
      "Hola!\n\n" +
      "Hoy tenes un partido de futbol.\n\n" +
      "Horario: " +
      horario +
      " hs\n" +
      "Presentate a las: " +
      llegada +
      " hs\n\n" +
      "Recorda llegar 10 minutos antes.\n\n" +
      "Nos vemos en la cancha!\n" +
      "ArmoLaCancha";

    servicioEmail.enviarCorreo(usuario.getEmail(), asunto, mensaje);
  }
}
