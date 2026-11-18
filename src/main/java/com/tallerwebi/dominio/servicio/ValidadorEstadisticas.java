package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.DTOEstadisticaJugador;
import com.tallerwebi.dominio.excepcion.EstadisticasNoGuardadas;
import java.util.List;

public class ValidadorEstadisticas {

  private static final int MAX_AMARILLAS = 2;
  private static final int MAX_ROJAS = 1;

  public void validarLista(List<DTOEstadisticaJugador> datosCargados) {
    if (datosCargados == null) {
      return;
    }
    for (DTOEstadisticaJugador dto : datosCargados) {
      if (dto != null) {
        validarJugador(dto);
      }
    }
  }

  public void validarJugador(DTOEstadisticaJugador dto) {
    validarNoNegativos(dto);
    validarTarjetas(valorOZero(dto.getAmarillas()), valorOZero(dto.getRojas()));
  }

  private void validarNoNegativos(DTOEstadisticaJugador dto) {
    if (
      valorOZero(dto.getGoles()) < 0 ||
      valorOZero(dto.getAsistencias()) < 0 ||
      valorOZero(dto.getAmarillas()) < 0 ||
      valorOZero(dto.getRojas()) < 0
    ) {
      throw new EstadisticasNoGuardadas("Los valores de las estadísticas no pueden ser negativos.");
    }
  }

  private void validarTarjetas(int amarillas, int rojas) {
    if (amarillas > MAX_AMARILLAS) {
      throw new EstadisticasNoGuardadas("Un jugador no puede tener más de 2 tarjetas amarillas.");
    }
    if (rojas > MAX_ROJAS) {
      throw new EstadisticasNoGuardadas("Un jugador no puede tener más de 1 tarjeta roja.");
    }
    if (amarillas == MAX_AMARILLAS && rojas != MAX_ROJAS) {
      throw new EstadisticasNoGuardadas(
        "Un jugador con 2 tarjetas amarillas debe tener 1 tarjeta roja asignada."
      );
    }
  }

  private int valorOZero(Integer valor) {
    return valor != null ? valor : 0;
  }
}
