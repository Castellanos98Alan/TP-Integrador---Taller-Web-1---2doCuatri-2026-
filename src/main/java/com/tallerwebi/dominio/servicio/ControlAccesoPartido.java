package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CodigoAccesoInvalidoException;
import java.util.function.BooleanSupplier;

public final class ControlAccesoPartido {

  private ControlAccesoPartido() {}

  static boolean tieneAcceso(Partido partido, Usuario usuario, boolean tieneInvitacion) {
    if (!Boolean.TRUE.equals(partido.getEsPrivado())) {
      return true;
    }

    // Si es privado, el usuario debe estar identificad
    if (usuario == null) {
      return false;
    }

    return esOrganizador(partido, usuario) || estaInscripto(partido, usuario) || tieneInvitacion;
  }

  static void validarCodigo(
    Partido partido,
    Usuario usuario,
    String codigoIngresado,
    boolean tieneInvitacion
  ) {
    if (tieneAcceso(partido, usuario, tieneInvitacion)) {
      return;
    }

    String codigoReal = partido.getCodigoDeAcceso();

    if (
      codigoIngresado == null ||
      codigoReal == null ||
      !codigoReal.equalsIgnoreCase(codigoIngresado.trim())
    ) {
      throw new CodigoAccesoInvalidoException("El código de acceso es incorrecto.");
    }
  }

  private static boolean esOrganizador(Partido partido, Usuario usuario) {
    return (
      partido.getOrganizador() != null && partido.getOrganizador().getId().equals(usuario.getId())
    );
  }

  private static boolean estaInscripto(Partido partido, Usuario usuario) {
    return partido
      .getConfirmados()
      .stream()
      .anyMatch(confirmado -> confirmado.getId().equals(usuario.getId()));
  }
}
