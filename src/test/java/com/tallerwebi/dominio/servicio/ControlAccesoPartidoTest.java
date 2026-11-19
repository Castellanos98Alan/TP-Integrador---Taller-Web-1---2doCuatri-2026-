package com.tallerwebi.dominio.servicio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CodigoAccesoInvalidoException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ControlAccesoPartidoTest {

  @Test
  public void deberiaPermitirAccesoSiElPartidoNoEsPrivado() {
    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(false);

    boolean resultado = ControlAccesoPartido.tieneAcceso(partido, null, false);

    assertThat(resultado, is(true));
  }

  @Test
  public void deberiaPermitirAccesoSiEsPrivadoEsNull() {
    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(null);

    boolean resultado = ControlAccesoPartido.tieneAcceso(partido, null, false);

    assertThat(resultado, is(true));
  }

  @Test
  public void deberiaDenegarAccesoSiEsPrivadoYUsuarioEsNull() {
    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(true);

    boolean resultado = ControlAccesoPartido.tieneAcceso(partido, null, true);

    assertThat(resultado, is(false));
  }

  @Test
  public void deberiaPermitirAccesoSiEsPrivadoYElUsuarioEsOrganizador() {
    Usuario organizador = new Usuario();
    organizador.setId(1L);

    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(true);
    when(partido.getOrganizador()).thenReturn(organizador);

    boolean resultado = ControlAccesoPartido.tieneAcceso(partido, organizador, false);

    assertThat(resultado, is(true));
  }

  @Test
  public void deberiaPermitirAccesoSiEsPrivadoYElUsuarioEstaInscripto() {
    Usuario organizador = new Usuario();
    organizador.setId(1L);

    Usuario inscripto = new Usuario();
    inscripto.setId(2L);

    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(true);
    when(partido.getOrganizador()).thenReturn(organizador);
    when(partido.getConfirmados()).thenReturn(List.of(inscripto));

    boolean resultado = ControlAccesoPartido.tieneAcceso(partido, inscripto, false);

    assertThat(resultado, is(true));
  }

  @Test
  public void deberiaPermitirAccesoSiEsPrivadoYTieneInvitacion() {
    Usuario invitado = new Usuario();
    invitado.setId(3L);

    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(true);
    when(partido.getOrganizador()).thenReturn(null);
    when(partido.getConfirmados()).thenReturn(Collections.emptyList());

    boolean resultado = ControlAccesoPartido.tieneAcceso(partido, invitado, true);

    assertThat(resultado, is(true));
  }

  @Test
  public void deberiaDenegarAccesoSiEsPrivadoYNoCumpleNingunaCondicion() {
    Usuario externo = new Usuario();
    externo.setId(4L);

    Usuario organizador = new Usuario();
    organizador.setId(1L);

    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(true);
    when(partido.getOrganizador()).thenReturn(organizador);
    when(partido.getConfirmados()).thenReturn(Collections.emptyList());

    boolean resultado = ControlAccesoPartido.tieneAcceso(partido, externo, false);

    assertThat(resultado, is(false));
  }

  @Test
  public void validarCodigoNoDeberiaLanzarExcepcionSiYaTieneAcceso() {
    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(false);

    assertDoesNotThrow(() -> ControlAccesoPartido.validarCodigo(partido, null, null, false));
  }

  @Test
  public void validarCodigoNoDeberiaLanzarExcepcionSiElCodigoEsValidoIgnorandoMayusculasYEspacios() {
    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(true);
    when(partido.getCodigoDeAcceso()).thenReturn("ABC123");

    assertDoesNotThrow(() -> ControlAccesoPartido.validarCodigo(partido, null, "  abc123  ", false)
    );
  }

  @Test
  public void validarCodigoDeberiaLanzarExcepcionSiCodigoIngresadoEsNull() {
    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(true);
    when(partido.getCodigoDeAcceso()).thenReturn("ABC123");

    CodigoAccesoInvalidoException ex = assertThrows(
      CodigoAccesoInvalidoException.class,
      () -> ControlAccesoPartido.validarCodigo(partido, null, null, false)
    );

    assertThat(ex.getMessage(), equalTo("El código de acceso es incorrecto."));
  }

  @Test
  public void validarCodigoDeberiaLanzarExcepcionSiCodigoRealEsNull() {
    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(true);
    when(partido.getCodigoDeAcceso()).thenReturn(null);

    assertThrows(
      CodigoAccesoInvalidoException.class,
      () -> ControlAccesoPartido.validarCodigo(partido, null, "ABC123", false)
    );
  }

  @Test
  public void validarCodigoDeberiaLanzarExcepcionSiElCodigoNoCoincide() {
    Partido partido = mock(Partido.class);
    when(partido.getEsPrivado()).thenReturn(true);
    when(partido.getCodigoDeAcceso()).thenReturn("ABC123");

    assertThrows(
      CodigoAccesoInvalidoException.class,
      () -> ControlAccesoPartido.validarCodigo(partido, null, "XYZ999", false)
    );
  }
}
