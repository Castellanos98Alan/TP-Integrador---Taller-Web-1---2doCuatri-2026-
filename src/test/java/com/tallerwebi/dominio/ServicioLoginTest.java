package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.repositorio.RepositorioJugador;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.ServicioLogin;
import com.tallerwebi.dominio.servicio.ServicioLoginImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioLoginTest {

  private ServicioLogin servicioLogin;
  private RepositorioUsuario repositorioUsuarioMock;
  private RepositorioJugador repositorioJugadorMock;

  @BeforeEach
  public void init() {
    repositorioUsuarioMock = mock(RepositorioUsuario.class);
    repositorioJugadorMock = mock(RepositorioJugador.class);

    // MODIFICADO: se usa el constructor principal con ambos repositorios.
    servicioLogin = new ServicioLoginImpl(repositorioUsuarioMock, repositorioJugadorMock);
  }

  @Test
  public void consultarUsuarioDeberiaLlamarAlRepositorio() {
    String email = "test@test.com";
    String password = "password";
    Usuario usuarioEsperado = new Usuario();

    when(repositorioUsuarioMock.buscarUsuario(email, password)).thenReturn(usuarioEsperado);

    Usuario usuarioObtenido = servicioLogin.consultarUsuario(email, password);

    assertThat(usuarioObtenido, equalTo(usuarioEsperado));
    verify(repositorioUsuarioMock).buscarUsuario(email, password);
  }

  @Test
  public void registrarUsuarioSiNoExisteDeberiaGuardarlo() throws UsuarioExistente {
    Usuario usuario = new Usuario();
    usuario.setEmail("nuevo@test.com");
    usuario.setPassword("123");

    when(repositorioUsuarioMock.buscarUsuario(usuario.getEmail(), usuario.getPassword()))
      .thenReturn(null);

    servicioLogin.registrar(usuario);

    verify(repositorioUsuarioMock).guardar(usuario);
  }

  @Test
  public void registrarUsuarioSiExisteDeberiaLanzarExcepcion() {
    Usuario usuario = new Usuario();
    usuario.setEmail("existe@test.com");
    usuario.setPassword("123");

    when(repositorioUsuarioMock.buscarUsuario(usuario.getEmail(), usuario.getPassword()))
      .thenReturn(new Usuario());

    assertThrows(UsuarioExistente.class, () -> servicioLogin.registrar(usuario));

    verify(repositorioUsuarioMock, never()).guardar(usuario);
  }

  // MODIFICADO: verifica que se cree y vincule el jugador al usuario.
  @Test
  public void registrarUsuarioConJugadorDeberiaGuardarAmbos() throws UsuarioExistente {
    Usuario usuario = new Usuario();
    usuario.setEmail("jugador@test.com");
    usuario.setPassword("123");

    when(repositorioUsuarioMock.buscarUsuario(usuario.getEmail(), usuario.getPassword()))
      .thenReturn(null);

    servicioLogin.registrar(usuario, "Juan", "Defensor", null);

    verify(repositorioUsuarioMock).guardar(usuario);

    verify(repositorioJugadorMock)
      .guardar(
        argThat(jugador ->
          jugador != null &&
          "Juan".equals(jugador.getNombre()) &&
          "Defensor".equals(jugador.getPosicion1()) &&
          "D".equals(jugador.getNivel()) &&
          jugador.getUsuario() == usuario
        )
      );
  }
}
